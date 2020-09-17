plugins {
    id("gradlebuild.distribution.api-java")
}

description = "JVM invocation and inspection abstractions"

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:process-services")

    testImplementation("org.gradle:native")
    testImplementation("org.gradle:core-api")
    testImplementation("org.gradle:file-collections")
    testImplementation("org.gradle:snapshots")
    testImplementation("org.gradle:resources")
    testImplementation(libs.slf4jApi)
    testImplementation(testFixtures("org.gradle:core"))
}
