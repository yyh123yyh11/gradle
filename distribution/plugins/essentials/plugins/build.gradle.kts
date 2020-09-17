import gradlebuild.cleanup.WhenNotEmpty
import gradlebuild.integrationtests.integrationTestUsesSampleDir

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
plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:workers")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:reporting")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-jvm")
    implementation("org.gradle:language-jvm")
    implementation("org.gradle:language-java")
    implementation("org.gradle:language-groovy")
    implementation("org.gradle:diagnostics")
    implementation("org.gradle:testing-base")
    implementation("org.gradle:testing-jvm")
    implementation("org.gradle:snapshots")

    implementation(libs.slf4jApi)
    implementation(libs.groovy)
    implementation(libs.ant)
    implementation(libs.asm)
    implementation(libs.guava)
    implementation(libs.commonsIo)
    implementation(libs.commonsLang)
    implementation(libs.inject)

    testImplementation("org.gradle:messaging")
    testImplementation("org.gradle:native")
    testImplementation("org.gradle:resources")
    testImplementation(libs.gson) {
        because("for unknown reason (bug in the Groovy/Spock compiler?) requires it to be present to use the Gradle Module Metadata test fixtures")
    }
    testImplementation(libs.jsoup)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:dependency-management"))
    testImplementation(testFixtures("org.gradle:resources-http"))
    testImplementation(testFixtures("org.gradle:platform-native"))
    testImplementation(testFixtures("org.gradle:language-jvm"))
    testImplementation(testFixtures("org.gradle:language-java"))
    testImplementation(testFixtures("org.gradle:language-groovy"))
    testImplementation(testFixtures("org.gradle:diagnostics"))

    testFixturesImplementation(testFixtures("org.gradle:core"))
    testFixturesImplementation("org.gradle:base-services-groovy")
    testFixturesImplementation("org.gradle:file-collections")
    testFixturesImplementation("org.gradle:language-jvm")
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation("org.gradle:process-services")
    testFixturesImplementation("org.gradle:resources")
    testFixturesImplementation(libs.guava)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-jvm")
}

strictCompile {
    ignoreRawTypes() // raw types used in public API
    ignoreDeprecations() // uses deprecated software model types
}

classycle {
    excludePatterns.set(listOf("org/gradle/**"))
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}

integrationTestUsesSampleDir("subprojects/plugins/src/main")
