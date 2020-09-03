plugins {
    id("gradlebuild.distribution.api-java")
}

description = "A set of general-purpose resource abstractions"

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:files")
    implementation("org.gradle:messaging")
    implementation("org.gradle:native")

    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsIo)

    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:core-api")
    testImplementation("org.gradle:file-collections")
    testImplementation("org.gradle:snapshots")

    testImplementation(testFixtures("org.gradle:core"))

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}
