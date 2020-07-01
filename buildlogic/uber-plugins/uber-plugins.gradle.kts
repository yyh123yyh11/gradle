plugins {
    id("buildlogic-conventions")
}

dependencies {
    implementation("gradlebuild:basics")
    implementation("gradlebuild:binary-compatibility")
    implementation("gradlebuild:buildquality")
    implementation("gradlebuild:cleanup")
    implementation("gradlebuild:dependency-modules")
    implementation("gradlebuild:kotlin-dsl")
    implementation("gradlebuild:jvm")
    implementation("gradlebuild:profiling")
    implementation("gradlebuild:publishing")

    implementation("org.gradle.kotlin:gradle-kotlin-dsl-conventions")
    implementation(kotlin("gradle-plugin"))
}
