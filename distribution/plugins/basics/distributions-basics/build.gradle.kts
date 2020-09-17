plugins {
    id("gradlebuild.distribution.packaging")
}

dependencies {
    coreRuntimeOnly(platform("org.gradle:core-platform"))

    pluginsRuntimeOnly(platform("org.gradle:distributions-core"))

    pluginsRuntimeOnly("org.gradle:resources-http")
    pluginsRuntimeOnly("org.gradle:resources-sftp")
    pluginsRuntimeOnly("org.gradle:resources-s3")
    pluginsRuntimeOnly("org.gradle:resources-gcs")
    pluginsRuntimeOnly("org.gradle:resources-http")
    pluginsRuntimeOnly("org.gradle:build-cache-http")

    pluginsRuntimeOnly("org.gradle:tooling-api-builders")
    pluginsRuntimeOnly("org.gradle:kotlin-dsl-tooling-builders")

    pluginsRuntimeOnly("org.gradle:test-kit")
}
