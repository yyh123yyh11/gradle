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

package org.gradle.configurationcache.serialization.codecs

import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.LocalFileDependencyBackedArtifactSet
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.ResolvedArtifactSet
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.ResolvedVariantSet
import org.gradle.api.internal.artifacts.transform.VariantSelector
import org.gradle.api.internal.artifacts.type.DefaultArtifactTypeRegistry
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.internal.file.FileCollectionInternal
import org.gradle.api.specs.Specs
import org.gradle.configurationcache.serialization.Codec
import org.gradle.configurationcache.serialization.ReadContext
import org.gradle.configurationcache.serialization.WriteContext
import org.gradle.configurationcache.serialization.decodePreservingSharedIdentity
import org.gradle.configurationcache.serialization.encodePreservingSharedIdentityOf
import org.gradle.configurationcache.serialization.readCollection
import org.gradle.configurationcache.serialization.readNonNull
import org.gradle.configurationcache.serialization.writeCollection
import org.gradle.internal.component.local.model.LocalFileDependencyMetadata
import org.gradle.internal.reflect.Instantiator

class LocalFileDependencyBackedArtifactSetCodec(
    private val instantiator: Instantiator,
    private val attributesFactory: ImmutableAttributesFactory
) : Codec<LocalFileDependencyBackedArtifactSet> {
    override suspend fun WriteContext.encode(value: LocalFileDependencyBackedArtifactSet) {
        // TODO - When the set of files is fixed (eg is `gradleApi()` or some hard-coded list of files):
        //   - calculate the attributes for each of the files eagerly rather than writing the mappings
        //   - when the selector would not apply a transform, then write only the files and nothing else
        //   - otherwise, write only the transform and attributes for each file rather than writing the transform registry
        write(value.dependencyMetadata.componentId)
        write(value.dependencyMetadata.files)

        // Write the file extension -> attributes mappings
        // TODO - move this to an encoder
        encodePreservingSharedIdentityOf(value.artifactTypeRegistry) {
            val mappings = value.artifactTypeRegistry.create()!!
            writeCollection(mappings) {
                writeString(it.name)
                write(it.attributes)
            }
        }
    }

    override suspend fun ReadContext.decode(): LocalFileDependencyBackedArtifactSet {
        val componentId = read() as ComponentIdentifier?
        val files = readNonNull<FileCollectionInternal>()

        // TODO - use an immutable registry implementation
        val artifactTypeRegistry = decodePreservingSharedIdentity {
            val registry = DefaultArtifactTypeRegistry(instantiator, attributesFactory, CollectionCallbackActionDecorator.NOOP)
            val mappings = registry.create()!!
            readCollection {
                val name = readString()
                val attributes = readNonNull<AttributeContainer>()
                val mapping = mappings.create(name).attributes
                @Suppress("UNCHECKED_CAST")
                for (attribute in attributes.keySet() as Set<Attribute<Any>>) {
                    mapping.attribute(attribute, attributes.getAttribute(attribute) as Any)
                }
            }
            registry
        }

        val selector = FixedVariantSelector()
        return LocalFileDependencyBackedArtifactSet(FixedFileMetadata(componentId, files), Specs.satisfyAll(), selector, artifactTypeRegistry)
    }
}


private
class FixedVariantSelector : VariantSelector {
    override fun select(candidates: ResolvedVariantSet, factory: VariantSelector.Factory): ResolvedArtifactSet {
        return candidates.variants.iterator().next().artifacts
    }
}


private
class FixedFileMetadata(val compId: ComponentIdentifier?, val source: FileCollectionInternal) : LocalFileDependencyMetadata {
    override fun getComponentId(): ComponentIdentifier? {
        return compId
    }

    override fun getFiles(): FileCollectionInternal {
        return source
    }

    override fun getSource(): FileCollectionDependency {
        throw UnsupportedOperationException("Should not be called")
    }
}
