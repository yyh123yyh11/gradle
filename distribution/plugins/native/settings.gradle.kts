pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
        maven { url = uri("https://repo.gradle.org/gradle/enterprise-libs-release-candidates-local") }
    }
}

file(".").listFiles()!!.filter { it.isDirectory && !it.name.startsWith(".") }.forEach { subproject ->
    include(subproject.name)
}

includeBuild("../../../build-src")

includeBuild("../publishing")
includeBuild("../jvm")
