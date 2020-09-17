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
    api("org.gradle:resources")
    implementation("org.gradle:base-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:core")
    implementation("org.gradle:model-core")
    implementation("org.gradle:logging")

    implementation(libs.commonsHttpclient)
    implementation(libs.slf4jApi)
    implementation(libs.jclToSlf4j)
    implementation(libs.jcifs)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.commonsIo)
    implementation(libs.xerces)
    implementation(libs.nekohtml)

    // testImplementation("org.gradle:internal-integ-testing")
    testImplementation(libs.jetty)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:logging"))

    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:logging")
    // testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation(libs.slf4jApi)

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}
