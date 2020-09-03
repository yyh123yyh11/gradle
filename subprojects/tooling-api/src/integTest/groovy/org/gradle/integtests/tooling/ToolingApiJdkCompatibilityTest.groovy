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
import org.gradle.internal.jvm.Jvm
import org.gradle.test.fixtures.file.TestFile
import org.gradle.tooling.GradleConnector
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition

class ToolingApiJdkCompatibilityTest extends AbstractIntegrationSpec {

    TestFile projectDir

    def setup() {
        projectDir = temporaryFolder.testDirectory
        temporaryFolder.testDirectory.file("build.gradle") << ""
        temporaryFolder.testDirectory.file("settings.gradle") << "rootProject.name = 'target-project'"
    }

    @Requires(adhoc = { AvailableJavaHomes.getJdk6() && AvailableJavaHomes.getJdk8() && TestPrecondition.JDK11_OR_LATER.fulfilled })
    def "compiler java 1.6 tapi client on java 11+ target Gradle on java 1.6"(String gradleVersion) {
        setup:
        def classesDir = new File("build/tmp/tapiCompatClasses")
        classesDir.mkdirs()

        // TODO We need to use java 1.8 compiler here with -target 6 argument because GradleConnector is compiled with java 8
        "compileJava${6}TapiClient" (classesDir)
        def classLoader = new URLClassLoader([classesDir.toURI().toURL()] as URL[], getClass().classLoader)
        def tapiClient = classLoader.loadClass("org.gradle.integtests.tooling.ToolingApiCompatibilityClient")

        when:
        def output = tapiClient.runHelp(gradleVersion, projectDir, AvailableJavaHomes.getJdk6().getJavaHome())

        then:
        output.contains("BUILD SUCCESSFUL")

        cleanup:
        classesDir.deleteDir()

        where:
        gradleVersion << ['2.6', '2.14.1'] // 2.6: minimum supported version for Tooling API; 2.14.1: last Gradle version that can run on Java 1.6
    }

    @Requires(adhoc = { AvailableJavaHomes.getJdk7() && AvailableJavaHomes.getJdk8() && TestPrecondition.JDK11_OR_LATER.fulfilled })
    def "compiler java 1.6 tapi client on java 11+ target Gradle on java 1.7"(String gradleVersion) {
        setup:
        def classesDir = new File("build/tmp/tapiCompatClasses")
        classesDir.mkdirs()

        // TODO We need to use java 1.8 compiler here with -target 7 argument because GradleConnector is compiled with java 8
        compileJava6TapiClient(classesDir)
        def classLoader = new URLClassLoader([classesDir.toURI().toURL()] as URL[], getClass().classLoader)
        def tapiClient = classLoader.loadClass("org.gradle.integtests.tooling.ToolingApiCompatibilityClient")

        when:
        def javaHome = AvailableJavaHomes.getJdk7().getJavaHome()
        // workaround for local sdkman issue
        File actualJavaHome = new File(javaHome, 'zulu-7.jdk/Contents/Home')
        if (actualJavaHome.exists() && actualJavaHome.isDirectory()) {
            javaHome = actualJavaHome
        }
        def output = tapiClient.runHelp(gradleVersion, projectDir, javaHome)

        then:
        output.contains("BUILD SUCCESSFUL")

        cleanup:
        classesDir.deleteDir()

        where:
        gradleVersion << ['2.6', '4.6', '4.10.3'] // 2.6: minimum supported version for Tooling API
                                                  // 4.6: last version with reported regression
                                                  // 4.10.3: last Gradle version that can run on Java 1.7
    }

    @Requires(adhoc = { AvailableJavaHomes.getJdk8() && TestPrecondition.JDK11_OR_LATER.fulfilled })
    def "compiler java 1.6 tapi client on java 11+ target Gradle on java 1.8"(String gradleVersion) {
        setup:
        def classesDir = new File("build/tmp/tapiCompatClasses")
        classesDir.mkdirs()

        compileJava6TapiClient(classesDir)
        def classLoader = new URLClassLoader([classesDir.toURI().toURL()] as URL[], getClass().classLoader)
        def tapiClient = classLoader.loadClass("org.gradle.integtests.tooling.ToolingApiCompatibilityClient")

        when:
        def javaHome = AvailableJavaHomes.getJdk8().getJavaHome()
        def output = tapiClient.runHelp(gradleVersion, projectDir, javaHome, distribution.gradleHomeDir.absoluteFile)

        then:
        GradleConnector.newConnector()
        output.contains("BUILD SUCCESSFUL")

        cleanup:
        classesDir.deleteDir()

        where:
        gradleVersion << ['2.6', '4.6', '4.7', 'current'] // 2.6: minimum supported version for Tooling API
                                                          // 4.6: last version with reported regression
                                                          // 4.7: first version that had no reported regression
    }

    private void compileJava6TapiClient(File targetDir) {
        compileJavaTapiClient(AvailableJavaHomes.getJdk8(), targetDir, '-source 6 -target 6')
    }

    private void compileJava7TapiClient(File targetDir) {
        compileJavaTapiClient(AvailableJavaHomes.getJdk8(), targetDir, '-source 7 -target 7')
    }

    private void compileJava8TapiClient(File targetDir) {
        compileJavaTapiClient(AvailableJavaHomes.getJdk8(), targetDir)
    }

    private void compileJavaTapiClient(Jvm jvm, File targetDir, String compilerArgs = '') {
        def javac = new File(jvm.getJavaHome(), "/bin/javac").absolutePath
        def classpath = System.getProperty("java.class.path")

        def sourcePath = getClass().classLoader.getResource("org/gradle/integtests/tooling/ToolingApiCompatibilityClient.java").file
        def compilationProcess = "$javac -cp $classpath -d ${targetDir.absolutePath} $compilerArgs $sourcePath".execute()

        ByteArrayOutputStream out = new ByteArrayOutputStream()
        ByteArrayOutputStream err = new ByteArrayOutputStream()
        compilationProcess.waitForProcessOutput(out, err)
        compilationProcess.waitFor()
        println out
        println err
    }
}
