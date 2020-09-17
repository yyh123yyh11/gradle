plugins {
    id("gradlebuild.distribution.api-java")
}

description = "Logging infrastructure"

gradlebuildJava.usedInWorkers()

dependencies {
    api(libs.slf4jApi)

    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:cli")
    implementation("org.gradle:build-option")

    implementation("org.gradle:native")
    implementation(libs.julToSlf4j)
    implementation(libs.ant)
    implementation(libs.commonsLang)
    implementation(libs.guava)
    implementation(libs.jansi)

    runtimeOnly(libs.log4jToSlf4j)
    runtimeOnly(libs.jclToSlf4j)

    testImplementation(testFixtures("org.gradle:core"))

    integTestImplementation(libs.ansiControlSequenceUtil)

    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation(testFixtures("org.gradle:core"))
    testFixturesImplementation(libs.slf4jApi)

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

classycle {
    excludePatterns.set(listOf("org/gradle/internal/featurelifecycle/**"))
}
