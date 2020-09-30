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

package org.gradle.integtests.tooling.r68

import org.gradle.integtests.tooling.fixture.TargetGradleVersion
import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.integtests.tooling.fixture.ToolingApiVersion

@ToolingApiVersion(">=6.8")
class ParallelActionExecutionCrossVersionSpec extends ToolingApiSpecification {
    def "action can run nested actions"() {
        given:
        settingsFile << """
            include 'a', 'b'
        """
        expect:
        def models = withConnection {
            action(new ActionRunsNestedActions()).run()
        }
        models.projectPaths == [':', ':a', ':b']
    }

    @TargetGradleVersion(">=6.8")
    def "nested actions run in parallel when target Gradle version supports it"() {
        expect: false
    }

    def "propagates nested action failures"() {
        expect: false
    }

    def "nested actions can request models that perform dependency resolution"() {
        expect: false
    }

}
