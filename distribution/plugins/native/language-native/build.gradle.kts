/*
 * Copyright 2014 the original author or authors.
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
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:files")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:snapshots")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-native")
    implementation("org.gradle:plugins")
    implementation("org.gradle:publish")
    implementation("org.gradle:maven")
    implementation("org.gradle:ivy")
    implementation("org.gradle:tooling-api")
    implementation("org.gradle:version-control")

    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.commonsIo)
    implementation(libs.inject)

    testFixturesApi("org.gradle:base-services") {
        because("Test fixtures export the Named class")
    }
    testFixturesApi("org.gradle:platform-base") {
        because("Test fixtures export the Platform class")
    }

    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation(testFixtures("org.gradle:platform-native"))

    testImplementation("org.gradle:native")
    testImplementation("org.gradle:resources")
    testImplementation("org.gradle:base-services-groovy")
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:version-control"))
    testImplementation(testFixtures("org.gradle:platform-native"))
    testImplementation(testFixtures("org.gradle:platform-base"))
    testImplementation(testFixtures("org.gradle:messaging"))
    testImplementation(testFixtures("org.gradle:snapshots"))

    integTestImplementation("org.gradle:native")
    integTestImplementation("org.gradle:resources")
    integTestImplementation(libs.nativePlatform)
    integTestImplementation(libs.ant)
    integTestImplementation(libs.jgit)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-native")
}

classycle {
    excludePatterns.set(listOf("org/gradle/language/nativeplatform/internal/**"))
}

integrationTestUsesSampleDir("subprojects/language-native/src/main")
