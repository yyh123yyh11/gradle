plugins {
    id("gradlebuild.distribution.implementation-java")
}

dependencies {
    implementation("org.gradle:launcher")
    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:native")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:base-services-groovy") // for 'Specs'
    implementation("org.gradle:testing-base")
    implementation("org.gradle:testing-jvm")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:reporting")
    implementation("org.gradle:workers")
    implementation("org.gradle:composite-builds")
    implementation("org.gradle:tooling-api")
    implementation("org.gradle:build-events")

    implementation(libs.groovy) // for 'Closure'
    implementation(libs.guava)
    implementation(libs.commonsIo)

    testImplementation("org.gradle:file-collections")
    testImplementation("org.gradle:platform-jvm")
}

strictCompile {
    ignoreDeprecations()
}
