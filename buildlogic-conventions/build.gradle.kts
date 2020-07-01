plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.gradle.kotlin:plugins:1.3.6")
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-conventions:0.5.0")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
