/*
 * Copyright 2010 the original author or authors.
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
import gradlebuild.cleanup.WhenNotEmpty
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:plugins")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-jvm")
    implementation("org.gradle:language-jvm")
    implementation("org.gradle:language-java")
    implementation("org.gradle:language-scala")
    implementation("org.gradle:scala")
    implementation("org.gradle:ear")
    implementation("org.gradle:tooling-api")

    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.commonsIo)
    implementation(libs.inject)

    testFixturesApi("org.gradle:base-services") {
        because("test fixtures export the Action class")
    }
    testFixturesApi("org.gradle:logging") {
        because("test fixtures export the ConsoleOutput class")
    }
    testFixturesImplementation("org.gradle:internal-integ-testing")

    testImplementation("org.gradle:dependency-management")
    testImplementation(libs.xmlunit)
    testImplementation(libs.equalsverifier)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:dependency-management"))

    integTestImplementation(libs.jetty)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-jvm")
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-jvm")
}

strictCompile {
    ignoreRawTypes()
}

classycle {
    excludePatterns.set(listOf(
        "org/gradle/plugins/ide/internal/*",
        "org/gradle/plugins/ide/eclipse/internal/*",
        "org/gradle/plugins/ide/idea/internal/*",
        "org/gradle/plugins/ide/eclipse/model/internal/*",
        "org/gradle/plugins/ide/idea/model/internal/*"))
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}

integrationTestUsesSampleDir("subprojects/ide/src/main")
