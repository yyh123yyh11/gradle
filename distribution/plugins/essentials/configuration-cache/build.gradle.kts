import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("gradlebuild.distribution.implementation-kotlin")
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-XXLanguage:+NewInference",
                "-XXLanguage:+SamConversionForKotlinFunctions"
            )
        }
    }

    processResources {
        //from({ project(":code-quality:configuration-cache-report").tasks.named("assembleReport") }) {
        //    into("org/gradle/configurationcache")
        //}
    }

    configCacheIntegTest {
        enabled = false
    }
}

afterEvaluate {
    // This is a workaround for the validate plugins task trying to inspect classes which have changed but are NOT tasks.
    // For the current project, we simply disable it since there are no tasks in there.
    tasks.withType<ValidatePlugins>().configureEach {
        enabled = false
    }
}

dependencies {
    implementation("org.gradle:base-services")
    implementation("org.gradle:base-services-groovy")
    implementation("org.gradle:core")
    implementation("org.gradle:core-api")
    implementation("org.gradle:dependency-management")
    implementation("org.gradle:execution")
    implementation("org.gradle:file-collections")
    implementation("org.gradle:kotlin-dsl")
    implementation("org.gradle:logging")
    implementation("org.gradle:messaging")
    implementation("org.gradle:model-core")
    implementation("org.gradle:native")
    implementation("org.gradle:persistent-cache")
    implementation("org.gradle:plugins")
    implementation("org.gradle:publish")
    implementation("org.gradle:resources")
    implementation("org.gradle:snapshots")
    implementation("org.gradle:plugin-use")

    // TODO - move the isolatable serializer to model-core to live with the isolatable infrastructure
    implementation("org.gradle:workers")

    // TODO - it might be good to allow projects to contribute state to save and restore, rather than have this project know about everything
    implementation("org.gradle:tooling-api")
    implementation("org.gradle:build-events")
    implementation("org.gradle:native")
    implementation("org.gradle:build-option")
    implementation("org.gradle:platform-jvm")

    implementation(libs.groovy)
    implementation(libs.slf4jApi)
    implementation(libs.guava)

    implementation(libs.futureKotlin("stdlib-jdk8"))
    implementation(libs.futureKotlin("reflect"))

    testImplementation(testFixtures("org.gradle:core"))
    testImplementation(libs.mockitoKotlin2)
    testImplementation(libs.kotlinCoroutinesDebug)

    integTestImplementation("org.gradle:jvm-services")
    integTestImplementation("org.gradle:tooling-api")
    integTestImplementation("org.gradle:platform-jvm")
    integTestImplementation("org.gradle:test-kit")
    integTestImplementation("org.gradle:launcher")

    integTestImplementation(libs.guava)
    integTestImplementation(libs.ant)
    integTestImplementation(libs.inject)
    integTestImplementation(testFixtures("org.gradle:dependency-management"))
    integTestImplementation(testFixtures("org.gradle:jacoco"))

    crossVersionTestImplementation("org.gradle:cli")

    testRuntimeOnly("org.gradle:distributions-core") {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly("org.gradle:distributions-jvm") {
        because("Includes tests for builds with TestKit involved; ConfigurationCacheJacocoIntegrationTest requires JVM distribution")
    }
    crossVersionTestDistributionRuntimeOnly("org.gradle:distributions-core")
}

classycle {
    excludePatterns.set(listOf("org/gradle/configurationcache/**"))
}
