
plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:files")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:workers")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-jvm")

    implementation(libs.groovy) // for 'Task.property(String propertyName) throws groovy.lang.MissingPropertyException'
    implementation(libs.guava)
    implementation(libs.inject)

    testImplementation("org.gradle:native")
    testImplementation("org.gradle:resources")
    testImplementation("org.gradle:snapshots")
    testImplementation(testFixtures("org.gradle:core"))

    testFixturesImplementation(libs.commonsLang)
    testFixturesImplementation(libs.guava)
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation(testFixtures("org.gradle:core"))

    testRuntimeOnly("org.gradle:distributions-core") {
        because("AbstractOptionsTest instantiates DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
}
