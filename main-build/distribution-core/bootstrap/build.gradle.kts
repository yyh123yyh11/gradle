plugins {
    id("gradlebuild.distribution.api-java")
}

gradlebuildJava.usedForStartup()

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:core")
    implementation("org.gradle:core-api")
    implementation("org.gradle:logging")
}
