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

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    api("org.gradle:core-api")

    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:messaging")
    implementation("org.gradle:snapshots")

    implementation(libs.futureKotlin("stdlib"))
    implementation(libs.inject)
    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.asm)

    testFixturesApi(testFixtures("org.gradle:diagnostics"))
    testFixturesApi(testFixtures("org.gradle:core"))
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation(libs.guava)

    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:file-collections")
    testImplementation("org.gradle:native")
    testImplementation("org.gradle:resources")
    testImplementation(testFixtures("org.gradle:core-api"))

    integTestImplementation("org.gradle:platform-base")

    testRuntimeOnly("org.gradle:distributions-core") {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

strictCompile {
    ignoreRawTypes() // raw types used in public API
}

classycle {
    excludePatterns.set(listOf(
        "org/gradle/model/internal/core/**",
        "org/gradle/model/internal/inspect/**",
        "org/gradle/api/internal/tasks/**",
        "org/gradle/model/internal/manage/schema/**",
        "org/gradle/model/internal/type/**",
        "org/gradle/api/internal/plugins/*"
    ))
}
