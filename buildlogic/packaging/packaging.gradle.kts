plugins {
    id("buildlogic-conventions")
}

dependencies {
    implementation("gradlebuild:documentation") {
        // TODO turn this around: move corresponding code to this project and let docs depend on it
        because("API metadata generation is part of the DSL guide")
    }
    implementation("gradlebuild:basics")
    implementation("gradlebuild:dependency-modules")
    implementation("gradlebuild:module-identity")

    implementation("com.google.code.gson:gson")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}
