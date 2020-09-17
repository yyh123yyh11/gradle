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
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:native")
    implementation("org.gradle:process-services")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:workers")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:diagnostics")

    implementation(libs.nativePlatform)
    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.commonsIo)
    implementation(libs.snakeyaml)
    implementation(libs.gson)
    implementation(libs.inject)

    testFixturesApi("org.gradle:resources")
    testFixturesApi(testFixtures("org.gradle:ide"))
    testFixturesImplementation(testFixtures("org.gradle:core"))
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation("org.gradle:native")
    testFixturesImplementation("org.gradle:platform-base")
    testFixturesImplementation("org.gradle:file-collections")
    testFixturesImplementation("org.gradle:process-services")
    testFixturesImplementation("org.gradle:snapshots")
    testFixturesImplementation(libs.guava)
    testFixturesImplementation(libs.nativePlatform)
    testFixturesImplementation(libs.commonsLang)
    testFixturesImplementation(libs.commonsIo)

    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:messaging"))
    testImplementation(testFixtures("org.gradle:platform-base"))
    testImplementation(testFixtures("org.gradle:model-core"))
    testImplementation(testFixtures("org.gradle:diagnostics"))
    testImplementation(testFixtures("org.gradle:base-services"))
    testImplementation(testFixtures("org.gradle:snapshots"))

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-native") {
        because("Required 'ideNative' to test visual studio project file generation for generated sources")
    }
}

classycle {
    excludePatterns.set(listOf(
        "org/gradle/nativeplatform/plugins/**",
        "org/gradle/nativeplatform/tasks/**",
        "org/gradle/nativeplatform/internal/resolve/**",
        "org/gradle/nativeplatform/toolchain/internal/**"
    ))
}

integrationTestUsesSampleDir("subprojects/platform-native/src/main")
