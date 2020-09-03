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
    id("gradlebuild.portalplugin.kotlin")
    id("gradlebuild.kotlin-dsl-plugin-extensions")
}

description = "Kotlin DSL Gradle Plugins deployed to the Plugin Portal"

group = "org.gradle.kotlin"
version = "1.3.7"

base.archivesBaseName = "plugins"

dependencies {
    compileOnly("org.gradle:base-services")
    compileOnly("org.gradle:logging")
    compileOnly("org.gradle:core-api")
    compileOnly("org.gradle:model-core")
    compileOnly("org.gradle:core")
    compileOnly("org.gradle:language-jvm")
    compileOnly("org.gradle:language-java")
    compileOnly("org.gradle:plugins")
    compileOnly("org.gradle:plugin-development")
    compileOnly("org.gradle:kotlin-dsl")

    compileOnly(libs.slf4jApi)
    compileOnly(libs.inject)

    implementation(libs.futureKotlin("stdlib-jdk8"))
    implementation(libs.futureKotlin("gradle-plugin"))
    implementation(libs.futureKotlin("sam-with-receiver"))

    integTestImplementation("org.gradle:base-services")
    integTestImplementation("org.gradle:logging")
    integTestImplementation("org.gradle:core-api")
    integTestImplementation("org.gradle:model-core")
    integTestImplementation("org.gradle:core")
    integTestImplementation("org.gradle:plugins")

    integTestImplementation("org.gradle:platform-jvm")
    integTestImplementation("org.gradle:kotlin-dsl")
    integTestImplementation("org.gradle:internal-testing")
    integTestImplementation(testFixtures("org.gradle:kotlin-dsl"))

    integTestImplementation(libs.slf4jApi)
    integTestImplementation(libs.mockitoKotlin)

    integTestDistributionRuntimeOnly("org.gradle:distributions-basics") {
        because("KotlinDslPluginTest tests against TestKit")
    }
    integTestLocalRepository(project)
}

classycle {
    excludePatterns.set(listOf("org/gradle/kotlin/dsl/plugins/base/**"))
}

// plugins ------------------------------------------------------------
pluginPublish {
    bundledGradlePlugin(
        name = "embeddedKotlin",
        shortDescription = "Embedded Kotlin Gradle Plugin",
        pluginId = "org.gradle.kotlin.embedded-kotlin",
        pluginClass = "org.gradle.kotlin.dsl.plugins.embedded.EmbeddedKotlinPlugin")

    bundledGradlePlugin(
        name = "kotlinDsl",
        shortDescription = "Gradle Kotlin DSL Plugin",
        pluginId = "org.gradle.kotlin.kotlin-dsl",
        pluginClass = "org.gradle.kotlin.dsl.plugins.dsl.KotlinDslPlugin")

    bundledGradlePlugin(
        name = "kotlinDslBase",
        shortDescription = "Gradle Kotlin DSL Base Plugin",
        pluginId = "org.gradle.kotlin.kotlin-dsl.base",
        pluginClass = "org.gradle.kotlin.dsl.plugins.base.KotlinDslBasePlugin")

    bundledGradlePlugin(
        name = "kotlinDslCompilerSettings",
        shortDescription = "Gradle Kotlin DSL Compiler Settings",
        pluginId = "org.gradle.kotlin.kotlin-dsl.compiler-settings",
        pluginClass = "org.gradle.kotlin.dsl.plugins.dsl.KotlinDslCompilerPlugins")

    bundledGradlePlugin(
        name = "kotlinDslPrecompiledScriptPlugins",
        shortDescription = "Gradle Kotlin DSL Precompiled Script Plugins",
        pluginId = "org.gradle.kotlin.kotlin-dsl.precompiled-script-plugins",
        pluginClass = "org.gradle.kotlin.dsl.plugins.precompiled.PrecompiledScriptPlugins")
}

// TODO:kotlin-dsl investigate
// See https://builds.gradle.org/viewLog.html?buildId=19024848&problemId=23230
tasks.noDaemonIntegTest.configure {
    enabled = false
}

// TODO:kotlin-dsl
testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}

// TODO: workaround for https://github.com/gradle/gradlecom/issues/627
//  which causes `publishPlugins` to fail with:
//  > java.io.FileNotFoundException: .../subprojects/kotlin-dsl-plugins/src/main/java (No such file or directory)
afterEvaluate {
    configurations.archives.get().allArtifacts.removeIf {
        it.name == "java"
    }
}
