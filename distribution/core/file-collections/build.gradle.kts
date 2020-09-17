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
plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:core-api")
    implementation("org.gradle:files")
    implementation("org.gradle:model-core")
    implementation("org.gradle:logging")
    implementation("org.gradle:native")

    implementation(libs.slf4jApi)
    implementation(libs.groovy)
    implementation(libs.guava)
    implementation(libs.commonsIo)
    implementation(libs.commonsLang)
    implementation(libs.inject)

    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:resources")
    testImplementation("org.gradle:snapshots")
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:core-api"))
    testImplementation(testFixtures("org.gradle:model-core"))

    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:core-api")
    testFixturesImplementation("org.gradle:native")

    testFixturesImplementation(libs.guava)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

strictCompile {
    ignoreRawTypes() // raw types used in public API
}

classycle {
    // Some cycles have been inherited from the time these classes were in :core
    excludePatterns.set(listOf("org/gradle/api/internal/file/collections/"))
}
