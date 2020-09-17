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
import gradlebuild.integrationtests.tasks.IntegrationTest

plugins {
    id("gradlebuild.distribution.api-java")
}

val integTestRuntimeResources by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = false
}
val integTestRuntimeResourcesClasspath by configurations.creating {
    extendsFrom(integTestRuntimeResources)
    isCanBeResolved = true
    isCanBeConsumed = false
    attributes {
        // play test apps MUST be found as exploded directory
        attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements::class.java, LibraryElements.RESOURCES))
    }
    isTransitive = false
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:ide")
    implementation("org.gradle:language-scala")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-jvm")
    implementation("org.gradle:platform-play")

    implementation(libs.groovy)
    implementation(libs.guava)

    integTestImplementation(testFixtures("org.gradle:platform-play"))
    integTestImplementation(testFixtures("org.gradle:ide"))

    integTestRuntimeResources(testFixtures("org.gradle:platform-play"))

    integTestDistributionRuntimeOnly("org.gradle:distributions-full")
}

strictCompile {
    ignoreDeprecations() // Play support in Gradle core has been deprecated
}

tasks.withType<IntegrationTest>().configureEach {
    dependsOn(":platform-play:integTestPrepare")
    // this is a workaround for which we need a better fix:
    // it sets the platform play test fixtures resources directory in front
    // of the classpath, so that we can find them when executing tests in
    // an exploded format, rather than finding them in the test fixtures jar
    classpath = integTestRuntimeResourcesClasspath + classpath
}
