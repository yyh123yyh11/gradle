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
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
    id("gradlebuild.publish-public-libraries")
    id("gradlebuild.shaded-jar")
}

shadedJar {
    shadedConfiguration.exclude(mapOf("group" to "org.slf4j", "module" to "slf4j-api"))
    keepPackages.set(listOf("org.gradle.tooling"))
    unshadedPackages.set(listOf("org.gradle", "org.slf4j", "sun.misc"))
    ignoredPackages.set(setOf("org.gradle.tooling.provider.model"))
}

dependencies {
    shadedImplementation(libs.slf4jApi)

    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:logging")
    implementation("org.gradle:core-api")
    implementation("org.gradle:core")
    implementation("org.gradle:wrapper")
    implementation("org.gradle:persistent-cache")

    implementation(libs.guava)

    testFixturesImplementation("org.gradle:core-api")
    testFixturesImplementation("org.gradle:core")
    testFixturesImplementation("org.gradle:model-core")
    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:base-services-groovy")
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation(libs.commonsIo)
    testFixturesImplementation(libs.slf4jApi)

    integTestImplementation("org.gradle:jvm-services")
    integTestImplementation("org.gradle:persistent-cache")

    crossVersionTestImplementation("org.gradle:jvm-services")
    crossVersionTestImplementation(libs.jetty)
    crossVersionTestImplementation(libs.commonsIo)
    crossVersionTestRuntimeOnly(libs.cglib) {
        because("BuildFinishedCrossVersionSpec classpath inference requires cglib enhancer")
    }

    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:logging"))
    testImplementation(testFixtures("org.gradle:dependency-management"))
    testImplementation(testFixtures("org.gradle:ide"))
    testImplementation(testFixtures("org.gradle:workers"))

    integTestNormalizedDistribution("org.gradle:distributions-full") {
        because("Used by ToolingApiRemoteIntegrationTest")
    }

    integTestDistributionRuntimeOnly("org.gradle:distributions-full")
    integTestLocalRepository(project(path)) {
        because("ToolingApiResolveIntegrationTest and ToolingApiClasspathIntegrationTest use the Tooling API Jar")
    }

    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-full")
    crossVersionTestLocalRepository(project(path)) {
        because("ToolingApiVersionSpecification uses the Tooling API Jar")
    }
}

strictCompile {
    ignoreRawTypes() // raw types used in public API
}

classycle {
    excludePatterns.set(listOf("org/gradle/tooling/**"))
}

apply(from = "buildship.gradle")

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}
integrationTestUsesSampleDir("subprojects/tooling-api/src/main")
