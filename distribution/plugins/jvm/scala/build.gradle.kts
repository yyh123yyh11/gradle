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
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:worker-processes")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:workers")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-jvm")
    implementation("org.gradle:language-jvm")
    implementation("org.gradle:language-java")
    implementation("org.gradle:language-scala")
    implementation("org.gradle:plugins")
    implementation("org.gradle:reporting")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:process-services")

    implementation(libs.groovy)
    implementation(libs.guava)
    implementation(libs.inject)

    testImplementation("org.gradle:base-services-groovy")
    testImplementation("org.gradle:files")
    testImplementation("org.gradle:resources")
    testImplementation(libs.slf4jApi)
    testImplementation(libs.commonsIo)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:plugins"))
    testImplementation(testFixtures("org.gradle:language-jvm"))
    testImplementation(testFixtures("org.gradle:language-java"))

    integTestImplementation("org.gradle:jvm-services")
    integTestImplementation(testFixtures("org.gradle:language-scala"))

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-jvm")
}

classycle {
    excludePatterns.set(listOf("org/gradle/api/internal/tasks/scala/**",
        // Unable to change package of public API
        "org/gradle/api/tasks/ScalaRuntime*"))
}

integrationTestUsesSampleDir("subprojects/scala/src/main")
