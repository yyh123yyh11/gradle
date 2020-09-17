/**
 * Process execution abstractions.
 */
plugins {
    id("gradlebuild.distribution.api-java")
}

gradlebuildJava.usedInWorkers()

dependencies {
    implementation("org.gradle:base-services")

    implementation("org.gradle:messaging")
    implementation("org.gradle:native")

    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.nativePlatform)

    testImplementation(testFixtures("org.gradle:core"))

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

classycle {
    excludePatterns.set(listOf("org/gradle/process/internal/**"))
}
