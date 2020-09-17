/*
 * Copyright 2019 the original author or authors.
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

gradlebuildJava.usedInWorkers()

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:native")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:jvm-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:reporting")
    implementation("org.gradle:diagnostics")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-jvm")
    implementation("org.gradle:language-java")
    implementation("org.gradle:testing-base")

    implementation(libs.slf4jApi)
    implementation(libs.groovy)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.commonsIo)
    implementation(libs.asm)
    implementation(libs.junit)
    implementation(libs.testng)
    implementation(libs.inject)
    implementation(libs.bsh)

    testImplementation("org.gradle:base-services-groovy")
    testImplementation(libs.guice) {
        because("This is for TestNG")
    }
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:testing-base"))
    testImplementation(testFixtures("org.gradle:diagnostics"))
    testImplementation(testFixtures("org.gradle:messaging"))
    testImplementation(testFixtures("org.gradle:base-services"))
    testImplementation(testFixtures("org.gradle:platform-native"))

    testRuntimeOnly("org.gradle:distributions-core") {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-jvm")
}

strictCompile {
    ignoreRawTypes() // raw types used in public API (org.gradle.api.tasks.testing.Test)
    ignoreDeprecations() // uses deprecated software model types
}

classycle {
    excludePatterns.set(listOf("org/gradle/api/internal/tasks/testing/**"))
}

tasks.named<Test>("test").configure {
    exclude("org/gradle/api/internal/tasks/testing/junit/AJunit*.*")
    exclude("org/gradle/api/internal/tasks/testing/junit/BJunit*.*")
    exclude("org/gradle/api/internal/tasks/testing/junit/ATestClass*.*")
    exclude("org/gradle/api/internal/tasks/testing/junit/ATestSetUp*.*")
    exclude("org/gradle/api/internal/tasks/testing/junit/ABroken*TestClass*.*")
    exclude("org/gradle/api/internal/tasks/testing/junit/ATestSetUpWithBrokenSetUp*.*")
    exclude("org/gradle/api/internal/tasks/testing/testng/ATestNGFactoryClass*.*")
}

integrationTestUsesSampleDir("subprojects/testing-jvm/src/main")
