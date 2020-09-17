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
    implementation("org.gradle:base-services-groovy") // for 'Specs'
    implementation("org.gradle:file-collections")
    implementation("org.gradle:resources")
    implementation("org.gradle:publish")
    implementation("org.gradle:plugins") // for base plugin to get archives conf
    implementation("org.gradle:plugin-use")
    implementation("org.gradle:dependency-management")

    implementation(libs.groovy) // for 'Closure' and 'Task.property(String propertyName) throws groovy.lang.MissingPropertyException'
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.inject)
    implementation(libs.ivy)

    testImplementation("org.gradle:native")
    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:snapshots")

    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:model-core"))
    testImplementation(testFixtures("org.gradle:platform-base"))
    testImplementation(testFixtures("org.gradle:dependency-management"))

    integTestImplementation("org.gradle:ear")
    integTestImplementation(libs.slf4jApi)
    integTestImplementation(libs.jetty)

    integTestRuntimeOnly("org.gradle:resources-s3")
    integTestRuntimeOnly("org.gradle:resources-sftp")
    integTestRuntimeOnly("org.gradle:api-metadata")

    testFixturesApi("org.gradle:base-services") {
        because("Test fixtures export the Action class")
    }
    testFixturesApi("org.gradle:core-api") {
        because("Test fixtures export the RepositoryHandler class")
    }
    testFixturesImplementation("org.gradle:logging")
    testFixturesImplementation("org.gradle:dependency-management")
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation(libs.slf4jApi)
    testFixturesImplementation(libs.sshdCore)
    testFixturesImplementation(libs.sshdScp)
    testFixturesImplementation(libs.sshdSftp)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-jvm")
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}


integrationTestUsesSampleDir("subprojects/ivy/src/main")
