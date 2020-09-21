plugins {
    id("gradlebuild.settings")
}

includeBuild("../../../build-src")

includeBuild("../publishing")
includeBuild("../jvm")
