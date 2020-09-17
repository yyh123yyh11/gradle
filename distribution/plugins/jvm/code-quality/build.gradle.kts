/*
 * Copyright 2010 the original author or authors.
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
    implementation("org.gradle:logging")
    implementation("org.gradle:native")
    implementation("org.gradle:process-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:plugins")
    implementation("org.gradle:workers")
    implementation("org.gradle:reporting")

    implementation(libs.groovy)
    implementation(libs.guava)
    implementation(libs.inject)
    implementation(libs.ant)

    testImplementation("org.gradle:file-collections")
    testImplementation(testFixtures("org.gradle:core"))

    testFixturesImplementation("org.gradle:core")
    testFixturesImplementation("org.gradle:core-api")
    testFixturesImplementation("org.gradle:base-services")

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-full")
}

classycle {
    excludePatterns.set(listOf("org/gradle/api/plugins/quality/internal/*"))
}
