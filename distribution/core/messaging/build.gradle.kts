plugins {
    id("gradlebuild.distribution.api-java")
}

gradlebuildJava.usedInWorkers()

dependencies {
    implementation("org.gradle:base-services")

    implementation(libs.fastutil)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.kryo)

    testImplementation(testFixtures("org.gradle:core"))

    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation(libs.slf4jApi)

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}
