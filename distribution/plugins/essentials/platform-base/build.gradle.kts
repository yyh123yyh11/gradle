import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:core-api")
    implementation("org.gradle:files")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:workers")
    implementation("org.gradle:execution")

    implementation(libs.groovy)
    implementation(libs.guava)
    implementation(libs.commonsLang)

    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:core-api"))
    testImplementation("org.gradle:native")
    testImplementation("org.gradle:snapshots")
    testImplementation("org.gradle:process-services")

    testFixturesApi("org.gradle:core")
    testFixturesApi("org.gradle:file-collections")
    testFixturesApi(testFixtures("org.gradle:model-core"))
    testFixturesImplementation(libs.guava)
    testFixturesApi(testFixtures("org.gradle:model-core"))
    testFixturesApi(testFixtures("org.gradle:diagnostics"))

    testRuntimeOnly("org.gradle:distributions-core") {
        because("RuntimeShadedJarCreatorTest requires a distribution to access the ...-relocated.txt metadata")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

classycle {
    excludePatterns.set(listOf("org/gradle/**"))
}

integrationTestUsesSampleDir("subprojects/platform-base/src/main")
