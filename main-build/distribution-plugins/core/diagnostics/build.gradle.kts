import gradlebuild.cleanup.WhenNotEmpty
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
    implementation("org.gradle:logging")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:reporting")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:snapshots")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:build-option")

    implementation(libs.slf4jApi)
    implementation(libs.groovy)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.inject)
    implementation(libs.jatl)

    testImplementation("org.gradle:process-services")
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:dependency-management"))
    testImplementation(testFixtures("org.gradle:logging"))

    integTestImplementation(libs.jsoup)
    integTestImplementation(libs.jetty)

    testFixturesApi(testFixtures("org.gradle:platform-native"))
    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation(libs.guava)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-full")  {
        because("There are integration tests that assert that all the tasks of a full distribution are reported (these should probably move to ':integTests').")
    }
}

classycle {
    excludePatterns.set(listOf(
        "org/gradle/api/reporting/model/internal/*",
        "org/gradle/api/reporting/dependencies/internal/*",
        "org/gradle/api/plugins/internal/*"))
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}
