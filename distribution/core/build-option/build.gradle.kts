plugins {
    id("gradlebuild.distribution.api-java")
}

gradlebuildJava.usedInWorkers()

dependencies {
    implementation("org.gradle:cli")

    implementation("org.gradle:base-annotations")
    implementation(libs.commonsLang)
}
