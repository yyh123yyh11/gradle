/*
 * Copyright 2017 the original author or authors.
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
    id("gradlebuild.jmh")
}

dependencies {
    api("org.gradle:build-cache-base")
    api("org.gradle:snapshots")

    implementation("org.gradle:base-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:files")
    implementation("org.gradle:native")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:resources")
    implementation("org.gradle:logging")

    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsIo)
    implementation(libs.inject)

    jmhImplementation(platform("org.gradle:distributions-dependencies"))
    jmhImplementation(libs.ant)
    jmhImplementation(libs.commonsCompress)
    jmhImplementation(libs.aircompressor)
    jmhImplementation(libs.snappy)
    jmhImplementation(libs.jtar)

    testImplementation("org.gradle:model-core")
    testImplementation("org.gradle:file-collections")
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:base-services"))

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}
