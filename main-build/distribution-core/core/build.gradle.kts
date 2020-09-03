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
import gradlebuild.cleanup.WhenNotEmpty

plugins {
    id("gradlebuild.distribution.api-java")
}

configurations {
    register("reports")
}

tasks.classpathManifest {
    optionalProjects.add("gradle-kotlin-dsl")
    // The gradle-runtime-api-info.jar is added by a 'distributions-...' project if it is on the (integration test) runtime classpath.
    // It contains information services in ':core' need to reason about the complete Gradle distribution.
    // To allow parts of ':core' code to be instantiated in unit tests without relying on this functionality, the dependency is optional.
    optionalProjects.add("gradle-runtime-api-info")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:messaging")
    implementation("org.gradle:logging")
    implementation("org.gradle:resources")
    implementation("org.gradle:cli")
    implementation("org.gradle:build-option")
    implementation("org.gradle:native")
    implementation("org.gradle:model-core")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:build-cache")
    implementation("org.gradle:build-cache-packaging")
    implementation("org.gradle:core-api")
    implementation("org.gradle:files")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:process-services")
    implementation("org.gradle:jvm-services")
    implementation("org.gradle:model-groovy")
    implementation("org.gradle:snapshots")
    implementation("org.gradle:file-watching")
    implementation("org.gradle:execution")
    implementation("org.gradle:worker-processes")
    implementation("org.gradle:normalization-java")

    implementation(libs.groovy)
    implementation(libs.ant)
    implementation(libs.guava)
    implementation(libs.inject)
    implementation(libs.asm)
    implementation(libs.asmCommons)
    implementation(libs.slf4jApi)
    implementation(libs.commonsIo)
    implementation(libs.commonsLang)
    implementation(libs.nativePlatform)
    implementation(libs.nativePlatformFileEvents)
    implementation(libs.commonsCompress)
    implementation(libs.xmlApis)

    testImplementation("org.gradle:plugins")
    testImplementation("org.gradle:testing-base")
    testImplementation("org.gradle:platform-native")
    testImplementation(libs.jsoup)
    testImplementation(libs.log4jToSlf4j)
    testImplementation(libs.jclToSlf4j)

    testFixturesApi("org.gradle:base-services") {
        because("test fixtures expose Action")
    }
    testFixturesApi("org.gradle:base-services-groovy") {
        because("test fixtures expose AndSpec")
    }
    testFixturesApi("org.gradle:core-api") {
        because("test fixtures expose Task")
    }
    testFixturesApi("org.gradle:logging") {
        because("test fixtures expose Logger")
    }
    testFixturesApi("org.gradle:model-core") {
        because("test fixtures expose IConventionAware")
    }
    testFixturesApi("org.gradle:build-cache") {
        because("test fixtures expose BuildCacheController")
    }
    testFixturesApi("org.gradle:execution") {
        because("test fixtures expose OutputChangeListener")
    }
    testFixturesApi("org.gradle:native") {
        because("test fixtures expose FileSystem")
    }
    testFixturesImplementation("org.gradle:file-collections")
    testFixturesImplementation("org.gradle:native")
    testFixturesImplementation("org.gradle:resources")
    testFixturesImplementation("org.gradle:process-services")
    testFixturesImplementation("org.gradle:messaging")
    testFixturesImplementation("org.gradle:persistent-cache")
    testFixturesImplementation("org.gradle:snapshots")
    testFixturesImplementation("org.gradle:normalization-java")
    testFixturesImplementation(libs.ivy)
    testFixturesImplementation(libs.slf4jApi)
    testFixturesImplementation(libs.guava)
    testFixturesImplementation(libs.ant)

    testFixturesRuntimeOnly("org.gradle:plugin-use") {
        because("This is a core extension module (see DynamicModulesClassPathProvider.GRADLE_EXTENSION_MODULES)")
    }
    testFixturesRuntimeOnly("org.gradle:dependency-management") {
        because("This is a core extension module (see DynamicModulesClassPathProvider.GRADLE_EXTENSION_MODULES)")
    }
    testFixturesRuntimeOnly("org.gradle:workers") {
        because("This is a core extension module (see DynamicModulesClassPathProvider.GRADLE_EXTENSION_MODULES)")
    }
    testFixturesRuntimeOnly("org.gradle:composite-builds") {
        because("We always need a BuildStateRegistry service implementation")
    }

    testImplementation("org.gradle:dependency-management")

    testImplementation(testFixtures("org.gradle:core-api"))
    testImplementation(testFixtures("org.gradle:messaging"))
    testImplementation(testFixtures("org.gradle:model-core"))
    testImplementation(testFixtures("org.gradle:logging"))
    testImplementation(testFixtures("org.gradle:base-services"))
    testImplementation(testFixtures("org.gradle:diagnostics"))

    integTestImplementation("org.gradle:workers")
    integTestImplementation("org.gradle:dependency-management")
    integTestImplementation("org.gradle:launcher")
    integTestImplementation("org.gradle:plugins")
    integTestImplementation(libs.jansi)
    integTestImplementation(libs.jetbrainsAnnotations)
    integTestImplementation(libs.jetty)
    integTestImplementation(libs.littleproxy)
    integTestImplementation(testFixtures("org.gradle:native"))

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder tests load services from a Gradle distribution.")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-basics") {
        because("Some tests utilise the 'java-gradle-plugin' and with that TestKit")
    }
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

strictCompile {
    ignoreRawTypes() // raw types used in public API
}

classycle {
    excludePatterns.set(listOf("org/gradle/**"))
}

tasks.test {
    setForkEvery(200)
}

tasks.compileTestGroovy {
    groovyOptions.fork("memoryInitialSize" to "128M", "memoryMaximumSize" to "1G")
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}
