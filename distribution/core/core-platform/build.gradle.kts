// Defines which JARs go into the core part (libs/*.jar) of a Gradle distribution (NOT libs/plugins/*.jar).
plugins {
    id("gradlebuild.platform")
}

javaPlatform.allowDependencies()

dependencies {
    runtime("org.gradle:installation-beacon")
    runtime("org.gradle:api-metadata")
    runtime("org.gradle:launcher") {
        because("This is the entry point of Gradle core which transitively depends on all other core projects.")
    }
    runtime("org.gradle:kotlin-dsl") {
        because("Adds support for Kotlin DSL scripts.")
    }
}
