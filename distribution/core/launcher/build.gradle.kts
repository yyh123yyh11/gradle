import gradlebuild.cleanup.WhenNotEmpty

plugins {
    id("gradlebuild.distribution.api-java")
    id("gradlebuild.launchable-jar")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:cli")
    implementation("org.gradle:messaging")
    implementation("org.gradle:build-option")
    implementation("org.gradle:native")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:files")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:snapshots")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:core-api")
    implementation("org.gradle:core")
    implementation("org.gradle:bootstrap")
    implementation("org.gradle:jvm-services")
    implementation("org.gradle:build-events")
    implementation("org.gradle:tooling-api")
    implementation("org.gradle:file-watching")

    implementation(libs.groovy) // for 'ReleaseInfo.getVersion()'
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsIo)
    implementation(libs.commonsLang)
    implementation(libs.asm)
    implementation(libs.ant)

    runtimeOnly(libs.asm)
    runtimeOnly(libs.commonsIo)
    runtimeOnly(libs.commonsLang)
    runtimeOnly(libs.slf4jApi)

    manifestClasspath("org.gradle:bootstrap")
    manifestClasspath("org.gradle:base-services")
    manifestClasspath("org.gradle:core-api")
    manifestClasspath("org.gradle:core")
    manifestClasspath("org.gradle:persistent-cache")

    testImplementation("org.gradle:internal-integ-testing")
    testImplementation("org.gradle:native")
    testImplementation("org.gradle:cli")
    testImplementation("org.gradle:process-services")
    testImplementation("org.gradle:core-api")
    testImplementation("org.gradle:model-core")
    testImplementation("org.gradle:resources")
    testImplementation("org.gradle:snapshots")
    testImplementation("org.gradle:base-services-groovy") // for 'Specs'

    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:language-java"))
    testImplementation(testFixtures("org.gradle:messaging"))
    testImplementation(testFixtures("org.gradle:logging"))
    testImplementation(testFixtures("org.gradle:tooling-api"))

    integTestImplementation("org.gradle:persistent-cache")
    integTestImplementation(libs.slf4jApi)
    integTestImplementation(libs.guava)
    integTestImplementation(libs.commonsLang)
    integTestImplementation(libs.commonsIo)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-native") {
        because("'native' distribution requried for 'ProcessCrashHandlingIntegrationTest.session id of daemon is different from daemon client'")
    }
}

strictCompile {
    ignoreRawTypes() // raw types used in public API
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}

// Needed for testing debug command line option (JDWPUtil) - 'CommandLineIntegrationSpec.can debug with org.gradle.debug=true'
val toolsJar = buildJvms.testJvm.map { jvm -> jvm.jdk.get().toolsClasspath }
dependencies {
    integTestRuntimeOnly(toolsJar)
}
