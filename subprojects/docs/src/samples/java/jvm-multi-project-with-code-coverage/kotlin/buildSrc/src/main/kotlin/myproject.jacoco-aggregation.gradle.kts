plugins {
    java
    jacoco
}

repositories {
    jcenter()
}

// A resolvable configuration to collect source code
val sourcesPath = jvm.createResolvableConfiguration("sourcesPath") {
    usingDependencyBucket("implementation")
    requiresAttributes {
        documentation("source-folders")
    }
}

// A resolvable configuration to collect JaCoCo coverage data
val coverageDataPath = jvm.createResolvableConfiguration("coverageDataPath") {
    usingDependencyBucket("implementation")
    requiresAttributes {
        documentation("jacoco-coverage-data")
    }
}

// Task to gather code coverage from multiple subprojects
val codeCoverageReport by tasks.registering(JacocoReport::class) {
    additionalClassDirs(configurations.runtimeClasspath.get())
    additionalSourceDirs(sourcesPath.incoming.artifactView { lenient(true) }.files)
    executionData(coverageDataPath.incoming.artifactView { lenient(true) }.files.filter { it.exists() })

    reports {
        // xml is usually used to integrate code coverage with
        // other tools like SonarQube, Coveralls or Codecov
        xml.isEnabled = true

        // HTML reports can be used to see code coverage
        // without any external tools
        html.isEnabled = true
    }
}

// Make JaCoCo report generation part of the 'check' lifecycle phase
tasks.check {
    dependsOn(codeCoverageReport)
}
