/*
 * Copyright 2012 the original author or authors.
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
    implementation("org.gradle:messaging")
    implementation("org.gradle:logging")
    implementation("org.gradle:files")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:core-api")
    implementation("org.gradle:core")
    implementation("org.gradle:resources")
    implementation("org.gradle:dependency-management")

    implementation(libs.guava)
    implementation(libs.inject)
    implementation(libs.jgit)
    implementation(libs.commonsHttpclient)
    implementation(libs.jsch)

    testImplementation("org.gradle:native")
    testImplementation("org.gradle:snapshots")
    testImplementation("org.gradle:process-services")
    testImplementation(testFixtures("org.gradle:core"))

    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:internal-integ-testing")

    testFixturesImplementation(libs.jgit)
    testFixturesImplementation(libs.commonsIo)
    testFixturesImplementation(libs.commonsHttpclient)
    testFixturesImplementation(libs.jsch)
    testFixturesImplementation(libs.guava)

    integTestImplementation("org.gradle:launcher")
    integTestDistributionRuntimeOnly("org.gradle:distributions-basics")
}
