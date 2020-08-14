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

import org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.ResolvableArtifact
import org.gradle.api.internal.artifacts.transform.ArtifactTransformListener
import org.gradle.api.internal.artifacts.transform.TransformationNode
import org.gradle.api.internal.artifacts.transform.TransformedArtifactSet
import org.gradle.instantexecution.serialization.Codec
import org.gradle.instantexecution.serialization.ReadContext
import org.gradle.instantexecution.serialization.WriteContext
import org.gradle.instantexecution.serialization.readNonNull
import org.gradle.instantexecution.serialization.withCodec
import org.gradle.internal.operations.BuildOperationExecutor


internal
class InitialTransformationNodeCodec(
    private val userTypesCodec: Codec<Any?>,
    private val buildOperationExecutor: BuildOperationExecutor,
    private val transformListener: ArtifactTransformListener
) : AbstractTransformationNodeCodec<TransformationNode.InitialTransformationNode>() {

    override suspend fun WriteContext.doEncode(value: TransformationNode.InitialTransformationNode) {
        withCodec(userTypesCodec) {
            write(value.source)
        }
        val artifactIndex = indexForArtifact(value)
        writeSmallInt(artifactIndex)
    }

    override suspend fun ReadContext.doDecode(): TransformationNode.InitialTransformationNode {
        val source = withCodec(userTypesCodec) {
            readNonNull<TransformedArtifactSet>()
        }
        val transformationStep = source.transformation.firstStep
        val artifactIndex = readSmallInt()
        val artifact = artifactWithIndex(source, artifactIndex)
        return TransformationNode.initial(transformationStep, artifact, source, buildOperationExecutor, transformListener)
    }

    private
    fun artifactWithIndex(source: TransformedArtifactSet, artifactIndex: Int): ResolvableArtifact {
        var artifact: ResolvableArtifact? = null
        var counter = 0
        source.visitSourceArtifacts {
            if (counter == artifactIndex) {
                artifact = it
            }
            counter++
        }
        return artifact!!
    }

    private
    fun indexForArtifact(value: TransformationNode.InitialTransformationNode): Int {
        var artifactIndex = -1
        var counter = 0
        value.source.visitSourceArtifacts {
            if (it == value.inputArtifact) {
                artifactIndex = counter
            }
            counter++
        }
        require(artifactIndex >= 0)
        return artifactIndex
    }
}
