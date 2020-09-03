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

description = "Execution engine that takes a unit of work and makes it happen"

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:core-api")
    implementation("org.gradle:files")
    implementation("org.gradle:snapshots")
    implementation("org.gradle:model-core")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:build-cache")
    implementation("org.gradle:build-cache-packaging")

    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsIo)
    implementation(libs.commonsLang)
    implementation(libs.inject)

    testImplementation("org.gradle:native")
    testImplementation("org.gradle:logging")
    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:model-core")
    testImplementation("org.gradle:base-services-groovy")
    testImplementation("org.gradle:resources")
    testImplementation(testFixtures("org.gradle:base-services"))
    testImplementation(testFixtures("org.gradle:file-collections"))
    testImplementation(testFixtures("org.gradle:messaging"))
    testImplementation(testFixtures("org.gradle:snapshots"))
    testImplementation(testFixtures("org.gradle:core"))

    testFixturesImplementation(libs.guava)
    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:build-cache")
    testFixturesImplementation("org.gradle:snapshots")
    testFixturesImplementation("org.gradle:model-core")

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}
