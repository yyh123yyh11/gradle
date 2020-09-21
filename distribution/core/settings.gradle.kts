plugins {
    id("gradlebuild.settings")
}

includeBuild("../../build-src")

includeBuild("../platform")
includeBuild("../fixtures")
