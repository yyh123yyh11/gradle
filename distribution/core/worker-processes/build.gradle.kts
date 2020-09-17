plugins {
    id("gradlebuild.distribution.implementation-java")
}

gradlebuildJava.usedInWorkers()

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:messaging")
    implementation("org.gradle:native")
    implementation("org.gradle:process-services")

    implementation(libs.slf4jApi)

    testImplementation(testFixtures("org.gradle:core"))
}
