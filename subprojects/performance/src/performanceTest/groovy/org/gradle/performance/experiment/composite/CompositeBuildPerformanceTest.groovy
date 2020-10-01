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

package org.gradle.performance.experiment.composite

import org.gradle.performance.AbstractCrossBuildPerformanceTest
import org.gradle.performance.categories.PerformanceExperiment
import org.gradle.performance.fixture.GradleBuildExperimentSpec
import org.gradle.profiler.BuildMutator
import org.gradle.profiler.ScenarioContext
import org.junit.experimental.categories.Category

@Category(PerformanceExperiment)
class CompositeBuildPerformanceTest extends AbstractCrossBuildPerformanceTest {

    def "configuration time"() {
        given:
        runner.testGroup = "Composite Builds"
        runner.buildSpec {
            displayName("Baseline")
            addBuildMutator { new GitCheckout(it.projectDir, "performance/baseline") }
        }
        runner.buildSpec {
            displayName("CompositeSimple")
            addBuildMutator { new GitCheckout(it.projectDir, "performance/compositeSimple") }
        }
        runner.buildSpec {
            displayName("CompositeBuildSrc")
            addBuildMutator { new GitCheckout(it.projectDir, "performance/compositeBuildSrc") }
        }
        runner.buildSpec {
            displayName("CompositeHierarchy")
            addBuildMutator { new GitCheckout(it.projectDir, "performance/compositeHierarchy") }
        }

        when:
        def results = runner.run()

        then:
        results
    }

    @Override
    protected void defaultSpec(GradleBuildExperimentSpec.GradleBuilder builder) {
        super.defaultSpec(builder)
        builder.warmUpCount = 3
        builder.invocationCount = 8
        builder.invocation {
            tasksToRun("help")
        }
    }

    class GitCheckout implements BuildMutator {

        private File dir
        private String commit

        GitCheckout(File dir, String commit) {
            this.dir = dir
            this.commit = commit
        }

        @Override
        void beforeScenario(ScenarioContext context) {
            System.out.println("Switching $dir to $commit")
            def reset = "git -C $dir reset --hard".execute()
            reset.waitForProcessOutput(System.out, System.err)
            def checkout = "git -C $dir checkout $commit".execute()
            checkout.waitForProcessOutput(System.out, System.err)
            def clean = "git -C $dir clean -xfd".execute() // make sure no empty 'buildSrc' folder exists
            clean.waitForProcessOutput(System.out, System.err)
        }
    }
}
