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
    implementation("org.gradle:process-services")
    implementation("org.gradle:files")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:execution")
    implementation("org.gradle:core")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:maven")
    implementation("org.gradle:ivy")
    implementation("org.gradle:platform-jvm")
    implementation("org.gradle:reporting")
    implementation("org.gradle:testing-base")
    implementation("org.gradle:testing-jvm")
    implementation("org.gradle:plugins")
    implementation("org.gradle:plugin-use")
    implementation("org.gradle:publish")
    implementation("org.gradle:messaging")
    implementation("org.gradle:workers")
    implementation("org.gradle:model-groovy")
    implementation("org.gradle:resources")

    implementation(libs.slf4jApi)
    implementation(libs.groovy)
    implementation(libs.commonsIo)
    implementation(libs.guava)
    implementation(libs.inject)
    implementation(libs.asm)

    testImplementation("org.gradle:file-collections")
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:logging"))

    integTestImplementation("org.gradle:base-services-groovy")
    integTestImplementation(libs.jetbrainsAnnotations)

    integTestLocalRepository("org.gradle:tooling-api") {
        because("Required by GradleImplDepsCompatibilityIntegrationTest")
    }

    testRuntimeOnly("org.gradle:distributions-basics") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-basics")
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-basics")
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}

integrationTestUsesSampleDir("subprojects/plugin-development/src/main")
