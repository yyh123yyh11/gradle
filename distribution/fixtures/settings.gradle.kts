// repositories for plugins
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            setUrl("https://repo.gradle.org/gradle/libs-releases")
        }
        maven {
            setUrl("https://repo.gradle.org/gradle/enterprise-libs-release-candidates-local")
        }
    }
}

// include all folders with a build script as subproject
settingsDir.listFiles()!!.filter { it.hasBuildScript() }.forEach { subproject ->
    include(subproject.name)
}

fun File.hasBuildScript() =
    File(this, "build.gradle.kts").exists() || File(this, "build.gradle").exists()

