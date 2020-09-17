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
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:ide")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-native")
    implementation("org.gradle:language-native")
    implementation("org.gradle:testing-base")
    implementation("org.gradle:testing-native")

    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.inject)
    implementation(libs.plist)

    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:platform-native"))
    testImplementation(testFixtures("org.gradle:language-native"))
    testImplementation(testFixtures("org.gradle:version-control"))

    integTestImplementation("org.gradle:native")
    integTestImplementation(libs.commonsIo)
    integTestImplementation(libs.jgit)

    testFixturesApi(testFixtures("org.gradle:ide"))
    testFixturesImplementation(libs.plist)
    testFixturesImplementation(libs.guava)
    testFixturesImplementation(testFixtures("org.gradle:ide"))

    testRuntimeOnly("org.gradle:distributions-core") {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-native")
}

integrationTestUsesSampleDir("subprojects/ide-native/src/main")
