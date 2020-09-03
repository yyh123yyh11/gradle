/*
 * Copyright 2019 the original author or authors.
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

description = "Shared classes for projects requiring GPG support"

dependencies {
    api("org.gradle:core-api")
    api("org.gradle:resources")
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:resources-http")
    implementation(libs.guava)

    api(libs.bouncycastlePgp)

    implementation(libs.groovy) {
        because("Project.exec() depends on Groovy")
    }

    testImplementation(testFixtures("org.gradle:core"))

    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation(libs.slf4jApi)
    testFixturesImplementation(libs.jetty)
    testFixturesImplementation(testFixtures("org.gradle:core"))
    testFixturesImplementation("org.gradle:internal-integ-testing")

    testRuntimeOnly("org.gradle:distributions-core") {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
}

classycle {
    excludePatterns.set(listOf("org/gradle/plugins/signing/type/pgp/**"))
}
