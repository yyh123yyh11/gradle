plugins {
    id("gradlebuild.Settings")
}

includeBuild("../../build-src")

includeBuild("../platform")
includeBuild("../fixtures")
