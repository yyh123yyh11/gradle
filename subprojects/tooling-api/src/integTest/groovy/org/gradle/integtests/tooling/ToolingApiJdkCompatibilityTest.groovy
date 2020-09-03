/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.integtests.tooling

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.AvailableJavaHomes
import org.gradle.integtests.tooling.fixture.ToolingApi
import org.gradle.internal.jvm.Jvm
import org.gradle.test.fixtures.file.TestFile
import org.gradle.tooling.ProjectConnection
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition

class ToolingApiJdkCompatibilityTest extends AbstractIntegrationSpec {

    ToolingApi toolingApi
    TestFile projectDir

    def setup() {
        toolingApi = new ToolingApi(distribution, temporaryFolder)
        projectDir = temporaryFolder.testDirectory
    }

    @Requires(adhoc = { AvailableJavaHomes.getJdk8() && TestPrecondition.JDK11_OR_LATER.fulfilled })
    def "compiler abd tapi client java11+ target Gradle 2.6 on java 1.8"() {
        setup:
        def java8Home = AvailableJavaHomes.getJdk8().getJavaHome()
        toolingApi.requireIsolatedDaemons()
        ProjectConnection connection = toolingApi.connector().forProjectDirectory(projectDir).useGradleVersion("2.6").connect()

        ByteArrayOutputStream out = new ByteArrayOutputStream()

        when:
        connection.newBuild()
            .forTasks("help")
            .setStandardOutput(out)
            .setJavaHome(java8Home)
            .run()

        then:
        out.toString().contains("BUILD SUCCESSFUL")
    }

    @Requires(adhoc = { AvailableJavaHomes.getJdk6() && AvailableJavaHomes.getJdk8() && TestPrecondition.JDK11_OR_LATER.fulfilled })
    def "compiler java 1.6 tapi client on java 11+ target Gradle 2.6 on java 1.6"() {
        setup:
        def classesDir = new File("build/tmp/tapiCompatClasses")
        classesDir.mkdirs()

        // TODO We need to use java 1.8 compiler here with -target 6 argument because GradleConnector is compiled with java 8
        compileJava6TapiClient(AvailableJavaHomes.getJdk8(), classesDir)
        def classLoader = new URLClassLoader([classesDir.toURI().toURL()] as URL[], getClass().classLoader)
        def tapiClient = classLoader.loadClass("org.gradle.integtests.tooling.ToolingApiCompatibilityClient")

        when:
        def output = tapiClient.runHelp("2.6", projectDir, AvailableJavaHomes.getJdk6().getJavaHome())

        then:
        output.contains("BUILD SUCCESSFUL")

        cleanup:
        classesDir.deleteDir()
    }

    private void compileJava6TapiClient(Jvm jdk8, File targetDir) {
        def javac = new File(jdk8.getJavaHome(), "/bin/javac").absolutePath
        def classpath = System.getProperty("java.class.path")

        def sourcePath = getClass().classLoader.getResource("org/gradle/integtests/tooling/ToolingApiCompatibilityClient.java").file
        def compilationProcess = "$javac -cp $classpath -d ${targetDir.absolutePath} -source 6 -target 6 $sourcePath".execute()

        ByteArrayOutputStream out = new ByteArrayOutputStream()
        ByteArrayOutputStream err = new ByteArrayOutputStream()
        compilationProcess.waitForProcessOutput(out, err)
        compilationProcess.waitFor()
        println out
        println err
    }
}
