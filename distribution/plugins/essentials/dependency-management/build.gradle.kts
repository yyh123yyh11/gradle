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
    id("gradlebuild.distribution.implementation-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:native")
    implementation("org.gradle:logging")
    implementation("org.gradle:files")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:build-cache")
    implementation("org.gradle:core")
    implementation("org.gradle:resources")
    implementation("org.gradle:resources-http")
    implementation("org.gradle:snapshots")
    implementation("org.gradle:execution")
    implementation("org.gradle:security")

    implementation(libs.slf4jApi)
    implementation(libs.groovy)
    implementation(libs.asm)
    implementation(libs.asmCommons)
    implementation(libs.asmUtil)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.commonsIo)
    implementation(libs.commonsHttpclient)
    implementation(libs.inject)
    implementation(libs.gson)
    implementation(libs.ant)
    implementation(libs.ivy)
    implementation(libs.maven3)

    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:diagnostics")
    testImplementation("org.gradle:build-cache-packaging")
    testImplementation(libs.nekohtml)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:messaging"))
    testImplementation(testFixtures("org.gradle:core-api"))
    testImplementation(testFixtures("org.gradle:version-control"))
    testImplementation(testFixtures("org.gradle:resources-http"))
    testImplementation(testFixtures("org.gradle:base-services"))
    testImplementation(testFixtures("org.gradle:snapshots"))
    testImplementation(testFixtures("org.gradle:execution"))

    integTestImplementation("org.gradle:build-option")
    integTestImplementation(libs.jansi)
    integTestImplementation(libs.ansiControlSequenceUtil)
    integTestImplementation(libs.jetty) {
        because("tests use HttpServlet directly")
    }
    integTestImplementation(testFixtures("org.gradle:security"))

    testFixturesApi("org.gradle:base-services") {
        because("Test fixtures export the Action class")
    }
    testFixturesApi("org.gradle:persistent-cache") {
        because("Test fixtures export the CacheAccess class")
    }

    testFixturesApi(libs.jetty)
    testFixturesImplementation("org.gradle:core")
    testFixturesImplementation(testFixtures("org.gradle:core"))
    testFixturesImplementation(testFixtures("org.gradle:resources-http"))
    testFixturesImplementation("org.gradle:core-api")
    testFixturesImplementation("org.gradle:messaging")
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation(libs.slf4jApi)
    testFixturesImplementation(libs.inject)
    testFixturesImplementation(libs.guava) {
        because("Groovy compiler reflects on private field on TextUtil")
    }
    testFixturesImplementation(libs.bouncycastlePgp)
    testFixturesApi(libs.testcontainersSpock) {
        because("API because of Groovy compiler bug leaking internals")
    }
    testFixturesImplementation("org.gradle:jvm-services") {
        because("Groovy compiler bug leaks internals")
    }

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-basics")
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

classycle {
    excludePatterns.set(listOf("org/gradle/**"))
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}

tasks.clean {
    val testFiles = layout.buildDirectory.dir("tmp/test files")
    doFirst {
        // On daemon crash, read-only cache tests can leave read-only files around.
        // clean now takes care of those files as well
        testFiles.get().asFileTree.matching {
            include("**/read-only-cache/**")
        }.visit { this.file.setWritable(true) }
    }
}

afterEvaluate {
    // This is a workaround for the validate plugins task trying to inspect classes which
    // have changed but are NOT tasks
    tasks.withType<ValidatePlugins>().configureEach {
        val main = sourceSets.main.get()
        classes.setFrom(main.output.classesDirs.asFileTree.filter { !it.isInternal(main) })
    }
}

fun File.isInternal(sourceSet: SourceSet) = isInternal(sourceSet.output.classesDirs.files)

fun File.isInternal(roots: Set<File>): Boolean = name == "internal" ||
    !roots.contains(parentFile) && parentFile.isInternal(roots)
