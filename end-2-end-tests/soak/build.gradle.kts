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

plugins {
    id("gradlebuild.internal.kotlin")
}

dependencies {
    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:core")
    testFixturesImplementation("org.gradle:internal-integ-testing")

    testImplementation(testFixtures("org.gradle:kotlin-dsl"))
    testImplementation(testFixtures("org.gradle:core"))

    integTestImplementation("org.gradle:logging")
    integTestImplementation("org.gradle:persistent-cache")
    integTestImplementation("org.gradle:launcher")
    integTestImplementation("org.gradle:file-watching")
    integTestImplementation(libs.slf4jApi)
    integTestImplementation(libs.jetty)

    integTestDistributionRuntimeOnly("org.gradle:distributions-full")
}

tasks.register("soakTest") {
    description = "Run all soak tests defined in the :soak subproject"
    group = "CI Lifecycle"
    dependsOn(":soak:embeddedIntegTest")
}
