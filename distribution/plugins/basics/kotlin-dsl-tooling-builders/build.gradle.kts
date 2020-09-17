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


plugins {
    id("gradlebuild.distribution.implementation-kotlin")
}

description = "Kotlin DSL Tooling Builders for IDEs"

dependencies {
    implementation("org.gradle:kotlin-dsl")

    implementation("org.gradle:base-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:resources")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-jvm")
    implementation("org.gradle:plugins")
    implementation("org.gradle:tooling-api")

    testImplementation(testFixtures("org.gradle:kotlin-dsl"))
    integTestImplementation("org.gradle:internal-testing")

    crossVersionTestImplementation("org.gradle:persistent-cache")
    crossVersionTestImplementation(libs.slf4jApi)
    crossVersionTestImplementation(libs.guava)
    crossVersionTestImplementation(libs.ant)

    integTestDistributionRuntimeOnly("org.gradle:distributions-basics")
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-basics")
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}
