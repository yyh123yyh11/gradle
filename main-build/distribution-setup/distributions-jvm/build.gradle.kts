plugins {
    id("gradlebuild.distribution.packaging")
}

dependencies {
    coreRuntimeOnly(platform("org.gradle:core-platform"))

    pluginsRuntimeOnly(platform("org.gradle:distributions-basics"))

    pluginsRuntimeOnly("org.gradle:scala")
    pluginsRuntimeOnly("org.gradle:ear")
    pluginsRuntimeOnly("org.gradle:code-quality")
    pluginsRuntimeOnly("org.gradle:jacoco")
    pluginsRuntimeOnly("org.gradle:ide")
}
