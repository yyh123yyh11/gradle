plugins {
    id("buildlogic-conventions")
}

dependencies {
    implementation("gradlebuild:basics")
    implementation("gradlebuild:cleanup")
    implementation("gradlebuild:dependency-modules")
    implementation("gradlebuild:module-identity")

    testImplementation("junit:junit")
}
