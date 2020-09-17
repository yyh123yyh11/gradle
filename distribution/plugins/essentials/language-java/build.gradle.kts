import gradlebuild.cleanup.WhenNotEmpty
import gradlebuild.integrationtests.integrationTestUsesSampleDir

plugins {
    id("gradlebuild.distribution.api-java")
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:messaging")
    implementation("org.gradle:logging")
    implementation("org.gradle:process-services")
    implementation("org.gradle:worker-processes")
    implementation("org.gradle:files")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:jvm-services")
    implementation("org.gradle:core-api")
    implementation("org.gradle:model-core")
    implementation("org.gradle:core")
    implementation("org.gradle:workers")
    implementation("org.gradle:snapshots")
    implementation("org.gradle:execution")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:platform-base")
    implementation("org.gradle:platform-jvm")
    implementation("org.gradle:language-jvm")
    implementation("org.gradle:build-events")
    implementation("org.gradle:tooling-api")

    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.commonsLang)
    implementation(libs.fastutil)
    implementation(libs.ant) // for 'ZipFile' and 'ZipEntry'
    implementation(libs.asm)
    implementation(libs.asmCommons)
    implementation(libs.inject)

    runtimeOnly("org.gradle:java-compiler-plugin")

    testImplementation("org.gradle:base-services-groovy")
    testImplementation(libs.commonsIo)
    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(testFixtures("org.gradle:platform-base"))

    testFixturesApi(testFixtures("org.gradle:language-jvm"))
    testFixturesImplementation("org.gradle:base-services")
    testFixturesImplementation("org.gradle:core")
    testFixturesImplementation("org.gradle:core-api")
    testFixturesImplementation("org.gradle:model-core")
    testFixturesImplementation("org.gradle:internal-integ-testing")
    testFixturesImplementation("org.gradle:platform-base")
    testFixturesImplementation("org.gradle:persistent-cache")
    testFixturesImplementation(libs.slf4jApi)

    testRuntimeOnly("org.gradle:distributions-core") {
        because("ProjectBuilder test (JavaLanguagePluginTest) loads services from a Gradle distribution.")
    }

    integTestDistributionRuntimeOnly("org.gradle:distributions-core")
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-basics")

    buildJvms.whenTestingWithEarlierThan(JavaVersion.VERSION_1_9) {
        val tools = it.jdk.get().toolsClasspath
        testRuntimeOnly(tools)
    }
}

strictCompile {
    ignoreDeprecations() // this project currently uses many deprecated part from 'platform-jvm'
}

classycle {
    // These public packages have classes that are tangled with the corresponding internal package.
    excludePatterns.set(listOf(
        "org/gradle/api/tasks/compile/**",
        "org/gradle/external/javadoc/**"
    ))
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}

integrationTestUsesSampleDir("subprojects/language-java/src/main")
