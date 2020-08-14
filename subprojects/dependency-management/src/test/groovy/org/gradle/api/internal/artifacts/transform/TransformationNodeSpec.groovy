/*
 * Copyright 2018 the original author or authors.
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

package org.gradle.api.internal.artifacts.transform

import org.gradle.api.Action
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.ResolvableArtifact
import org.gradle.api.internal.tasks.TaskDependencyContainer
import org.gradle.execution.plan.Node
import org.gradle.execution.plan.TaskDependencyResolver
import org.gradle.internal.operations.BuildOperationExecutor
import spock.lang.Specification

class TransformationNodeSpec extends Specification {

    def artifact = Mock(ResolvableArtifact)
    def source = Mock(TransformedArtifactSet)
    def dependencyResolver = Mock(TaskDependencyResolver)
    def hardSuccessor = Mock(Action)
    def transformationStep = Mock(TransformationStep)
    def graphDependenciesResolver = Mock(ExecutionGraphDependenciesResolver)
    def buildOperationExecutor = Mock(BuildOperationExecutor)
    def transformListener = Mock(ArtifactTransformListener)

    def "initial node adds dependency on artifact node and dependencies"() {
        def transformerDependencies = Stub(TaskDependencyContainer)
        def artifactNode = node()
        def dependenciesNode = node()
        def transformerNode = node()

        given:
        _ * source.dependenciesResolver >> graphDependenciesResolver
        def node = TransformationNode.initial(transformationStep, artifact, source, buildOperationExecutor, transformListener)

        when:
        node.resolveDependencies(dependencyResolver, hardSuccessor)

        then:
        1 * dependencyResolver.resolveDependenciesFor(null, transformationStep) >> [transformerNode]
        1 * hardSuccessor.execute(transformerNode)
        1 * graphDependenciesResolver.computeDependencyNodes(transformationStep) >> transformerDependencies
        1 * dependencyResolver.resolveDependenciesFor(null, transformerDependencies) >> [dependenciesNode]
        1 * hardSuccessor.execute(dependenciesNode)
        1 * dependencyResolver.resolveDependenciesFor(null, artifact) >> [artifactNode]
        1 * hardSuccessor.execute(artifactNode)
        0 * _
    }

    def "chained node with empty extra resolver only adds dependency on previous step and dependencies"() {
        def transformerDependencies = Stub(TaskDependencyContainer)
        def dependenciesNode = node()
        def transformerNode = node()

        _ * source.dependenciesResolver >> graphDependenciesResolver
        def initialNode = TransformationNode.initial(Stub(TransformationStep), artifact, source, buildOperationExecutor, transformListener)

        given:
        def node = TransformationNode.chained(transformationStep, initialNode, buildOperationExecutor, transformListener)

        when:
        node.resolveDependencies(dependencyResolver, hardSuccessor)

        then:
        1 * dependencyResolver.resolveDependenciesFor(null, transformationStep) >> [transformerNode]
        1 * hardSuccessor.execute(transformerNode)
        1 * graphDependenciesResolver.computeDependencyNodes(transformationStep) >> transformerDependencies
        1 * dependencyResolver.resolveDependenciesFor(null, transformerDependencies) >> [dependenciesNode]
        1 * hardSuccessor.execute(dependenciesNode)
        1 * hardSuccessor.execute(initialNode)
        0 * _
    }

    private Node node() {
        def node = Stub(Node)
        _ * node.dependencyPredecessors >> new LinkedHashSet<Node>()
        return node
    }

}
