plugins {
    java
    jacoco
}

version = "1.0.2"
group = "org.gradle.sample"

repositories {
    jcenter()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

// Do not generate reports for individual projects
tasks.jacocoTestReport {
    enabled = false
}

jvm {
    // Share sources folder with other projects for aggregated JaCoCo reports
    createOutgoingElements("transitiveSourcesElements") {
        providesAttributes {
            documentation("source-folders")
        }
        extendsFrom(configurations.implementation)
        sourceSets.main.get().java.srcDirs.forEach(::artifact)
    }

    // Share the coverage data to be aggregated for the whole product
    createOutgoingElements("coverageDataElements") {
        providesAttributes { documentation("jacoco-coverage-data") }
        extendsFrom(configurations.implementation)
        artifact(tasks.test.map { task ->
            task.extensions.getByType<JacocoTaskExtension>().destinationFile!!
        })
    }
}
