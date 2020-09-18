plugins {
    id("com.gradle.enterprise").version("3.4.1")
    id("com.gradle.enterprise.gradle-enterprise-conventions-plugin").version("0.7.1")
}

rootProject.name = "gradle"

includeBuild("build-src")

includeBuild("distribution/plugins/full")
includeBuild("portal-plugins")

includeBuild("code-quality")
includeBuild("documentation")
includeBuild("end-2-end-tests")
