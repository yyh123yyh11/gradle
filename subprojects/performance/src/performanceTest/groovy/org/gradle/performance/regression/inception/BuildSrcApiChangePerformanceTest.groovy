/*
 * Copyright 2017 the original author or authors.
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
package org.gradle.performance.regression.inception

import org.gradle.performance.AbstractCrossVersionPerformanceTest
import org.gradle.performance.categories.SlowPerformanceRegressionTest
import org.gradle.performance.mutator.ApplyAbiChangeToGroovySourceFileMutator
import org.gradle.performance.mutator.ApplyNonAbiChangeToGroovySourceFileMutator
import org.gradle.profiler.BuildMutator
import org.gradle.profiler.InvocationSettings
import org.junit.experimental.categories.Category
import spock.lang.Ignore

@Ignore // TODO (donat) there's a memory leak probably. Will investigate later.
@Category(SlowPerformanceRegressionTest)
class BuildSrcApiChangePerformanceTest extends AbstractCrossVersionPerformanceTest {

    def setup() {
        def targetVersion = "6.8-20200927220040+0000"
        runner.targetVersions = [targetVersion]
        runner.minimumBaseVersion = "6.8"
        runner.warmUpRuns = 3
    }

    def "buildSrc abi change"() {
        given:
        runner.tasksToRun = ['help']
        runner.runs = determineNumberOfRuns(runner.testProject)
        runner.gradleOpts = runner.projectMemoryOptions

        and:
        def changingClassFilePath = "buildSrc/src/main/groovy/ChangingClass.groovy"
        runner.addBuildMutator { new CreateChangingClassMutator(it, changingClassFilePath) }
        runner.addBuildMutator { new ApplyAbiChangeToGroovySourceFileMutator(new File(it.projectDir, changingClassFilePath)) }

        when:
        def result = runner.run()

        then:
        result.assertCurrentVersionHasNotRegressed()
    }

    def "buildSrc non-abi change"() {
        given:
        runner.tasksToRun = ['help']
        runner.runs = determineNumberOfRuns(runner.testProject)
        runner.gradleOpts = runner.projectMemoryOptions

        and:
        def changingClassFilePath = "buildSrc/src/main/groovy/ChangingClass.groovy"
        runner.addBuildMutator { new CreateChangingClassMutator(it, changingClassFilePath) }
        runner.addBuildMutator { new ApplyNonAbiChangeToGroovySourceFileMutator(new File(it.projectDir, changingClassFilePath)) }

        when:
        def result = runner.run()
        then:
        result.assertCurrentVersionHasNotRegressed()
    }

    private static int determineNumberOfRuns(String testProject) {
        switch (testProject) {
            case 'mediumMonolithicJavaProject':
                return 40
            case 'largeJavaMultiProject':
                return 20
            case 'largeJavaMultiProjectKotlinDsl':
                return 10
            default:
                20
        }
    }

    private static class CreateChangingClassMutator implements BuildMutator {

        CreateChangingClassMutator(InvocationSettings settings, String filePath) {
            new File(settings.projectDir, filePath).with {
                parentFile.mkdirs()
                // We need to create the file in the constructor, since the file change mutators read the text of the file in the constructor as well.
                // It would be better if the file change mutators would read the original test in `beforeScenario`, so we could create the file here
                // as well in beforeScenario.
                text = """
                    class ChangingClass {
                        void changingMethod() {
                            System.out.println("Do the thing");
                        }
                    }
                """
            }
        }
    }
}
