import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
    id("gradlebuild.jmh")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:logging")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:execution")
    implementation("org.gradle:process-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:diagnostics")
    implementation("org.gradle:normalization-java")
    implementation("org.gradle:resources")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:native")

    implementation(libs.groovy)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.commonsCompress)
    implementation(libs.commonsIo)
    implementation(libs.inject)
    implementation(libs.asm)
    implementation(libs.nativePlatform)

    testImplementation("org.gradle:snapshots")
    testImplementation(libs.ant)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:diagnostics"))
    testImplementation(testFixtures("org.gradle:logging"))
    testImplementation(testFixtures("org.gradle:platform-base"))
    testImplementation(testFixtures("org.gradle:platform-native"))

    integTestImplementation(libs.slf4jApi)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

strictCompile {
    ignoreDeprecations() // most of this project has been deprecated
}

integrationTestUsesSampleDir("subprojects/platform-jvm/src/main")
