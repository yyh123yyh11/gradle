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

plugins {
    id("gradlebuild.distribution.api-kotlin")
    id("gradlebuild.kotlin-dsl-dependencies-embedded")
}

description = "Kotlin DSL Provider"

dependencies {
    api("org.gradle:kotlin-dsl-tooling-models")
    api("org.gradle:kotlin-compiler-embeddable")
    api(libs.futureKotlin("stdlib-jdk8"))

    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:native")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:base-services-groovy") // for 'Specs'
    implementation("org.gradle:file-collections")
    implementation("org.gradle:files")
    implementation("org.gradle:resources")
    implementation("org.gradle:build-cache")
    implementation("org.gradle:tooling-api")
    implementation("org.gradle:execution")

    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.inject)

    implementation(libs.futureKotlin("scripting-common")) {
        isTransitive = false
    }
    implementation(libs.futureKotlin("scripting-jvm")) {
        isTransitive = false
    }
    implementation(libs.futureKotlin("scripting-jvm-host-embeddable")) {
        isTransitive = false
    }
    implementation(libs.futureKotlin("scripting-compiler-embeddable")) {
        isTransitive = false
    }
    implementation(libs.futureKotlin("scripting-compiler-impl-embeddable")) {
        isTransitive = false
    }
    implementation(libs.futureKotlin("sam-with-receiver-compiler-plugin")) {
        isTransitive = false
    }
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.1.0") {
        isTransitive = false
    }

    testImplementation("org.gradle:build-cache-http")
    testImplementation("org.gradle:build-init")
    testImplementation("org.gradle:jacoco")
    testImplementation("org.gradle:platform-native") {
        because("BuildType from platform-native is used in ProjectAccessorsClassPathTest")
    }
    testImplementation("org.gradle:plugins")
    testImplementation("org.gradle:version-control")
    testImplementation(libs.ant)
    testImplementation(libs.asm)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.jacksonKotlin)

    testImplementation(libs.archunit)
    testImplementation(libs.kotlinCoroutines)
    testImplementation(libs.awaitility)

    integTestImplementation("org.gradle:language-groovy")
    integTestImplementation("org.gradle:language-groovy") {
        because("ClassBytesRepositoryTest makes use of Groovydoc task.")
    }
    integTestImplementation("org.gradle:internal-testing")
    integTestImplementation(libs.mockitoKotlin)

    testRuntimeOnly("org.gradle:distributions-native") {
        because("SimplifiedKotlinScriptEvaluator reads default imports from the distribution (default-imports.txt) and BuildType from platform-native is used in ProjectAccessorsClassPathTest.")
    }

    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:core-api")
    testFixturesImplementation("org.gradle:core")
    testFixturesImplementation("org.gradle:resources")
    testFixturesImplementation("org.gradle:kotlin-dsl-tooling-builders")
    testFixturesImplementation("org.gradle:test-kit")
    testFixturesImplementation("org.gradle:internal-testing")
    testFixturesImplementation("org.gradle:internal-integ-testing")

    testFixturesImplementation(libs.junit)
    testFixturesImplementation(libs.mockitoKotlin)
    testFixturesImplementation(libs.jacksonKotlin)
    testFixturesImplementation(libs.asm)

    integTestDistributionRuntimeOnly("org.gradle:distributions-basics")
}

classycle {
    excludePatterns.set(listOf("org/gradle/kotlin/dsl/**"))
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}
