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

plugins {
    id("gradlebuild.distribution.implementation-java")
    id("gradlebuild.publish-public-libraries")
}

description = "Tools to take immutable, comparable snapshots of files and other things"

dependencies {
    api("org.gradle:files")
    api("org.gradle:hashing")

    implementation("org.gradle:base-annotations")

    implementation(libs.guava)
    implementation(libs.slf4jApi)

    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:resources")
    testImplementation("org.gradle:native")
    testImplementation("org.gradle:persistent-cache")
    testImplementation(libs.ant)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:core-api"))
    testImplementation(testFixtures("org.gradle:base-services"))
    testImplementation(testFixtures("org.gradle:file-collections"))
    testImplementation(testFixtures("org.gradle:messaging"))

    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:core-api")
    testFixturesImplementation("org.gradle:file-collections")

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

afterEvaluate {
    // This is a workaround for the validate plugins task trying to inspect classes which have changed but are NOT tasks.
    // For the current project, we exclude all internal packages, since there are no tasks in there.
    tasks.withType<ValidatePlugins>().configureEach {
        classes.setFrom(sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/internal/**") })
    }
}
