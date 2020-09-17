plugins {
    id("gradlebuild.distribution.packaging")
}

dependencies {
    coreRuntimeOnly(platform("org.gradle:core-platform"))

    pluginsRuntimeOnly(platform("org.gradle:distributions-jvm")) {
        because("the project dependency 'toolingNative -> ide' currently links this to the JVM ecosystem")
    }
    pluginsRuntimeOnly(platform("org.gradle:distributions-publishing")) {
        because("configuring publishing is part of the 'language native' support")
    }

    pluginsRuntimeOnly("org.gradle:language-native")
    pluginsRuntimeOnly("org.gradle:tooling-native")
    pluginsRuntimeOnly("org.gradle:ide-native")
    pluginsRuntimeOnly("org.gradle:testing-native")
}
