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
import gradlebuild.cleanup.WhenNotEmpty
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:resources")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:plugins")
    implementation("org.gradle:plugin-use")
    implementation("org.gradle:publish")

    implementation(libs.slf4jApi)
    implementation(libs.groovy)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.inject)
    implementation(libs.ant)
    implementation(libs.ivy)
    implementation(libs.maven3)
    implementation(libs.pmavenCommon)
    implementation(libs.pmavenGroovy)
    implementation(libs.maven3WagonFile)
    implementation(libs.maven3WagonHttp)
    implementation(libs.plexusContainer)
    implementation(libs.aetherConnector)

    testImplementation("org.gradle:native")
    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:snapshots")
    testImplementation("org.gradle:resources-http")
    testImplementation(libs.xmlunit)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:model-core"))
    testImplementation(testFixtures("org.gradle:dependency-management"))

    integTestImplementation("org.gradle:ear")
    integTestImplementation(libs.jetty)

    testFixturesApi("org.gradle:base-services") {
        because("Test fixtures export the Action class")
    }
    testFixturesImplementation("org.gradle:core-api")
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation("org.gradle:dependency-management")

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-jvm")
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

strictCompile {
    ignoreDeprecations() // old 'maven' publishing mechanism: types are deprecated
    ignoreRawTypes() // old 'maven' publishing mechanism: raw types used in public API
}

classycle {
    excludePatterns.set(listOf(
        "org/gradle/api/publication/maven/internal/**",
        "org/gradle/api/artifacts/maven/**"))
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}

integrationTestUsesSampleDir("subprojects/maven/src/main")
