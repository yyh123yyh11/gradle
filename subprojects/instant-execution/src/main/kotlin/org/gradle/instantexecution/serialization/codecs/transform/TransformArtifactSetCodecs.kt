/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.instantexecution.serialization.codecs.transform

import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.artifacts.DefaultResolvedArtifact
import org.gradle.api.internal.artifacts.PreResolvedResolvableArtifact
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.ResolvableArtifact
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.ResolvedArtifactSet
import org.gradle.api.internal.artifacts.transform.AbstractTransformedArtifactSet
import org.gradle.api.internal.artifacts.transform.ArtifactTransformDependencies
import org.gradle.api.internal.artifacts.transform.ExecutionGraphDependenciesResolver
import org.gradle.api.internal.artifacts.transform.ExtraExecutionGraphDependenciesResolverFactory
import org.gradle.api.internal.artifacts.transform.Transformation
import org.gradle.api.internal.artifacts.transform.TransformationNodeRegistry
import org.gradle.api.internal.artifacts.transform.TransformationStep
import org.gradle.api.internal.artifacts.transform.TransformedExternalArtifactSet
import org.gradle.api.internal.artifacts.transform.TransformedProjectArtifactSet
import org.gradle.api.internal.artifacts.transform.Transformer
import org.gradle.api.internal.attributes.ImmutableAttributes
import org.gradle.api.internal.tasks.TaskDependencyContainer
import org.gradle.api.internal.tasks.TaskDependencyResolveContext
import org.gradle.instantexecution.serialization.Codec
import org.gradle.instantexecution.serialization.ReadContext
import org.gradle.instantexecution.serialization.WriteContext
import org.gradle.instantexecution.serialization.decodePreservingSharedIdentity
import org.gradle.instantexecution.serialization.encodePreservingSharedIdentityOf
import org.gradle.instantexecution.serialization.readCollection
import org.gradle.instantexecution.serialization.readFile
import org.gradle.instantexecution.serialization.readNonNull
import org.gradle.instantexecution.serialization.writeCollection
import org.gradle.instantexecution.serialization.writeFile
import org.gradle.internal.Try
import org.gradle.internal.component.local.model.ComponentFileArtifactIdentifier
import org.gradle.internal.component.model.DefaultIvyArtifactName
import org.gradle.internal.operations.BuildOperationQueue
import org.gradle.internal.operations.RunnableBuildOperation
import java.io.File
import java.util.function.Consumer


abstract class AbstractTransformedArtifactSetCodec<T : AbstractTransformedArtifactSet>(
    private val transformationNodeRegistry: TransformationNodeRegistry
) : Codec<T> {
    override suspend fun WriteContext.encode(value: T) {
        encodePreservingSharedIdentityOf(value) {
            write(value.ownerId)
            write(value.targetVariantAttributes)
            write(value.transformation)
            encodeArtifacts(value)
            val unpacked = unpackTransformation(value.transformation, value.dependenciesResolver)
            write(unpacked.dependencies)
        }
    }

    override suspend fun ReadContext.decode(): T {
        return decodePreservingSharedIdentity {
            val ownerId = readNonNull<ComponentIdentifier>()
            val targetAttributes = readNonNull<ImmutableAttributes>()
            val transformation = readNonNull<Transformation>()
            val artifacts = decodeArtifacts(ownerId)
            val dependencies = readNonNull<List<TransformDependencies>>()
            val dependenciesPerTransformer = mutableMapOf<Transformer, TransformDependencies>()
            transformation.visitTransformationSteps {
                dependenciesPerTransformer.put(it.transformer, dependencies[dependenciesPerTransformer.size])
            }
            createInstance(ownerId, artifacts, targetAttributes, transformation, FixedDependenciesResolverFactory(dependenciesPerTransformer), transformationNodeRegistry)
        }
    }

    abstract suspend fun WriteContext.encodeArtifacts(value: T)

    abstract suspend fun ReadContext.decodeArtifacts(ownerId: ComponentIdentifier): ResolvedArtifactSet

    abstract
    fun createInstance(ownerId: ComponentIdentifier, sourceArtifacts: ResolvedArtifactSet, targetAttributes: ImmutableAttributes, transformation: Transformation, dependenciesResolverFactory: ExtraExecutionGraphDependenciesResolverFactory, transformationNodeRegistry: TransformationNodeRegistry): T
}


class TransformedExternalArtifactSetCodec(
    transformationNodeRegistry: TransformationNodeRegistry
) : AbstractTransformedArtifactSetCodec<TransformedExternalArtifactSet>(transformationNodeRegistry) {

    override suspend fun WriteContext.encodeArtifacts(value: TransformedExternalArtifactSet) {
        val files = mutableListOf<File>()
        value.visitSourceArtifacts { files.add(it.file) }
        write(files)
    }

    override suspend fun ReadContext.decodeArtifacts(ownerId: ComponentIdentifier): ResolvedArtifactSet {
        val files = readNonNull<List<File>>()
        return FixedFilesArtifactSet(ownerId, files)
    }

    override fun createInstance(ownerId: ComponentIdentifier, sourceArtifacts: ResolvedArtifactSet, targetAttributes: ImmutableAttributes, transformation: Transformation, dependenciesResolverFactory: ExtraExecutionGraphDependenciesResolverFactory, transformationNodeRegistry: TransformationNodeRegistry): TransformedExternalArtifactSet {
        return TransformedExternalArtifactSet(ownerId, sourceArtifacts, targetAttributes, transformation, dependenciesResolverFactory, transformationNodeRegistry)
    }
}


class TransformedProjectArtifactSetCodec(
    transformationNodeRegistry: TransformationNodeRegistry
) : AbstractTransformedArtifactSetCodec<TransformedProjectArtifactSet>(transformationNodeRegistry) {
    override fun createInstance(ownerId: ComponentIdentifier, sourceArtifacts: ResolvedArtifactSet, targetAttributes: ImmutableAttributes, transformation: Transformation, dependenciesResolverFactory: ExtraExecutionGraphDependenciesResolverFactory, transformationNodeRegistry: TransformationNodeRegistry): TransformedProjectArtifactSet {
        return TransformedProjectArtifactSet(ownerId, sourceArtifacts, targetAttributes, transformation, dependenciesResolverFactory, transformationNodeRegistry)
    }

    override suspend fun WriteContext.encodeArtifacts(value: TransformedProjectArtifactSet) {
        val artifacts = mutableListOf<DefaultResolvedArtifact>()
        value.visitSourceArtifacts { artifacts.add(it as DefaultResolvedArtifact) }
        writeCollection(artifacts) { artifact ->
            writeFile(artifact.file)
            writeString(artifact.artifactName.name)
            writeString(artifact.artifactName.type)
            writeNullableString(artifact.artifactName.extension)
            writeNullableString(artifact.artifactName.classifier)
        }
    }

    override suspend fun ReadContext.decodeArtifacts(ownerId: ComponentIdentifier): ResolvedArtifactSet {
        val artifacts = mutableListOf<ResolvableArtifact>()
        readCollection {
            val file = readFile()
            val artifactName = DefaultIvyArtifactName(readString(), readString(), readNullableString(), readNullableString())
            artifacts.add(PreResolvedResolvableArtifact(null, artifactName, ComponentFileArtifactIdentifier(ownerId, file.name), file, TaskDependencyContainer.EMPTY))
        }
        return FixedArtifactSet(artifacts)
    }
}


private
class FixedDependenciesResolverFactory(private val dependencies: Map<Transformer, TransformDependencies>) : ExtraExecutionGraphDependenciesResolverFactory, ExecutionGraphDependenciesResolver {
    override fun create(componentIdentifier: ComponentIdentifier): ExecutionGraphDependenciesResolver {
        return this
    }

    override fun computeDependencyNodes(transformationStep: TransformationStep): TaskDependencyContainer {
        throw UnsupportedOperationException("should not be called")
    }

    override fun selectedArtifacts(transformer: Transformer): FileCollection {
        throw UnsupportedOperationException("should not be called")
    }

    override fun computeArtifacts(transformer: Transformer): Try<ArtifactTransformDependencies> {
        return dependencies.getValue(transformer).recreate().computeArtifacts(transformer)
    }
}


private
class FixedFilesArtifactSet(private val ownerId: ComponentIdentifier, private val files: List<File>) : ResolvedArtifactSet {
    override fun visitDependencies(context: TaskDependencyResolveContext) {
        throw UnsupportedOperationException("should not be called")
    }

    override fun startVisit(actions: BuildOperationQueue<RunnableBuildOperation>, listener: ResolvedArtifactSet.AsyncArtifactListener): ResolvedArtifactSet.Completion {
        throw UnsupportedOperationException("should not be called")
    }

    override fun visitArtifacts(visitor: Consumer<ResolvableArtifact>) {
        for (file in files) {
            visitor.accept(PreResolvedResolvableArtifact(null, DefaultIvyArtifactName.forFile(file, null), ComponentFileArtifactIdentifier(ownerId, file.name), file, TaskDependencyContainer.EMPTY))
        }
    }
}


private
class FixedArtifactSet(private val artifacts: List<ResolvableArtifact>) : ResolvedArtifactSet {
    override fun visitDependencies(context: TaskDependencyResolveContext) {
        throw UnsupportedOperationException("should not be called")
    }

    override fun startVisit(actions: BuildOperationQueue<RunnableBuildOperation>, listener: ResolvedArtifactSet.AsyncArtifactListener): ResolvedArtifactSet.Completion {
        throw UnsupportedOperationException("should not be called")
    }

    override fun visitArtifacts(visitor: Consumer<ResolvableArtifact>) {
        for (artifact in artifacts) {
            visitor.accept(artifact)
        }
    }
}
