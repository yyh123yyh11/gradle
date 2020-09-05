/*
 * Copyright 2019 the original author or authors.
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

package org.gradle.performance.fixture

import org.gradle.integtests.fixtures.executer.IntegrationTestBuildContext
import org.gradle.integtests.fixtures.versions.ReleasedVersionDistributions
import org.gradle.performance.results.CrossVersionPerformanceResults
import org.gradle.performance.results.DataReporter
import org.gradle.performance.results.ResultsStore
import org.gradle.profiler.BuildAction
import org.gradle.profiler.BuildMutator
import org.gradle.profiler.GradleInvoker
import org.gradle.profiler.InvocationSettings
import org.gradle.profiler.ToolingApiInvoker
import org.gradle.tooling.LongRunningOperation
import org.gradle.tooling.ProjectConnection

import java.util.function.Consumer
import java.util.function.Function

/**
 * Runs cross version performance tests using Gradle profiler.
 */
class GradleProfilerCrossVersionPerformanceTestRunner extends AbstractCrossVersionPerformanceTestRunner {

    private final List<Function<InvocationSettings, BuildMutator>> buildMutators = []
    private final List<String> measuredBuildOperations = []
    BuildAction buildAction

    GradleProfilerCrossVersionPerformanceTestRunner(BuildExperimentRunner experimentRunner, ResultsStore resultsStore, DataReporter<CrossVersionPerformanceResults> reporter, ReleasedVersionDistributions releases, IntegrationTestBuildContext buildContext) {
        super(experimentRunner, resultsStore, reporter, releases, buildContext)
    }

    @Override
    protected void configureGradleBuildExperimentSpec(GradleBuildExperimentSpec.GradleBuilder builder) {
        builder
            .buildMutators(buildMutators)
            .measuredBuildOperations(measuredBuildOperations)
            .invocation {
                delegate.buildAction = this.buildAction
            }
    }

    void addBuildMutator(Function<InvocationSettings, BuildMutator> buildMutator) {
        buildMutators.add(buildMutator)
    }

    List<String> getMeasuredBuildOperations() {
        return measuredBuildOperations
    }

    def <T extends LongRunningOperation> ToolingApiAction<T> toolingApi(String displayName, Function<ProjectConnection, T> initialAction) {
        useToolingApi = true
        def tapiAction = new ToolingApiAction<T>(displayName, initialAction)
        this.buildAction = tapiAction
        return tapiAction
    }
}

class ToolingApiAction<T extends LongRunningOperation> implements BuildAction {
    private final Function<ProjectConnection, T> initialAction
    private final String displayName
    private Consumer<T> tapiAction

    ToolingApiAction(String displayName, Function<ProjectConnection, T> initialAction) {
        this.displayName = displayName
        this.initialAction = initialAction
    }

    void run(Consumer<T> tapiAction) {
        this.tapiAction = tapiAction
    }

    @Override
    boolean isDoesSomething() {
        return true
    }

    @Override
    String getDisplayName() {
        return displayName
    }

    @Override
    String getShortDisplayName() {
        return displayName
    }

    @Override
    void run(GradleInvoker buildInvoker, List<String> gradleArgs, List<String> jvmArgs) {
        // TODO: Add a public API to configure this in a nice way on the Gradle profiler side
        def toolingApiInvoker = (ToolingApiInvoker) buildInvoker
        def projectConnection = toolingApiInvoker.projectConnection
        def longRunningOperation = initialAction.apply(projectConnection)
        toolingApiInvoker.run(longRunningOperation) { builder ->
            builder.setJvmArguments(jvmArgs)
            builder.withArguments(gradleArgs)
            tapiAction.accept(builder)
        }
    }
}
