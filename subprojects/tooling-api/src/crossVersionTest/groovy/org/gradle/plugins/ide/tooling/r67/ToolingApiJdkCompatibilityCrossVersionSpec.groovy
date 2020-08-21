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

package org.gradle.plugins.ide.tooling.r67

import org.gradle.integtests.fixtures.AvailableJavaHomes
import org.gradle.integtests.tooling.fixture.TargetGradleVersion
import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.internal.jvm.Jvm
import org.gradle.tooling.model.gradle.GradleBuild
import org.gradle.util.Requires

class ToolingApiJdkCompatibilityCrossVersionSpec extends ToolingApiSpecification {

    @Requires(adhoc = { AvailableJavaHomes.getJdk7() })
    @TargetGradleVersion(">=2.6 <5.0")
    def "can run build using JDK7"() {
        given:
        def jdkHome = AvailableJavaHomes.getJdk7()
        settingsFile << "rootProject.name = 'root'"
        buildFile << """
            import ${Jvm.canonicalName}    
            assert Jvm.current().getJavaVersion().java7
            System.out.println("Hello world")
        """
        def outputStream = new ByteArrayOutputStream()
        when:
        withConnection {connection ->
            def build = connection.newBuild()
            build.forTasks("help").setJavaHome(jdkHome.javaHome)
            build.standardOutput = outputStream
            build.run()
        }
        then:
        outputStream.toString().contains("Hello world")
    }

    @Requires(adhoc = { AvailableJavaHomes.getJdk7() })
    @TargetGradleVersion(">=2.6 <5.0")
    def "can request model from build using JDK7"() {
        given:
        def jdk = AvailableJavaHomes.getJdk7()
        settingsFile << "rootProject.name = 'root'"
        buildFile << """
            import ${Jvm.canonicalName}    
            assert Jvm.current().getJavaVersion().java7
        """

        expect:
        withConnection {connection ->
            connection.model(GradleBuild).setJavaHome(jdk.javaHome).get()
        }
    }

    @Requires(adhoc = { AvailableJavaHomes.getJdk8() })
    def "can run build using JDK8"() {
        given:
        def jdkHome = AvailableJavaHomes.getJdk8()
        settingsFile << "rootProject.name = 'root'"
        buildFile << """
            import ${Jvm.canonicalName}    
            assert Jvm.current().getJavaVersion().java8
            System.out.println("Hello world")
        """
        def outputStream = new ByteArrayOutputStream()
        when:
        withConnection {connection ->
            def build = connection.newBuild()
            build.forTasks("help").setJavaHome(jdkHome.javaHome)
            build.standardOutput = outputStream
            build.run()
        }
        then:
        outputStream.toString().contains("Hello world")
    }

    @Requires(adhoc = { AvailableJavaHomes.getJdk8() })
    def "can request model from build using JDK8"() {
        given:
        def jdk = AvailableJavaHomes.getJdk8()
        settingsFile << "rootProject.name = 'root'"
        buildFile << """
            import ${Jvm.canonicalName}    
            assert Jvm.current().getJavaVersion().java8
        """

        expect:
        withConnection {connection ->
            connection.model(GradleBuild).setJavaHome(jdk.javaHome).get()
        }
    }
}
