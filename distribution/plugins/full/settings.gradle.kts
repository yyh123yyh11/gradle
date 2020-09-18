plugins {
    id("gradlebuild.Settings")
}

includeBuild("../../../build-src")

includeBuild("../publishing")
includeBuild("../jvm")
includeBuild("../native")
