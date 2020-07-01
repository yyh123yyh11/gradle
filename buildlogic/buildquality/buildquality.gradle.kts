plugins {
    id("buildlogic-conventions")
}

dependencies {
    implementation("gradlebuild:basics")
    implementation("gradlebuild:binary-compatibility")
    implementation("gradlebuild:cleanup")
    implementation("gradlebuild:documentation")
    implementation("gradlebuild:integration-testing")
    implementation("gradlebuild:performance-testing")
    implementation("gradlebuild:profiling")

    implementation("me.champeau.gradle:japicmp-gradle-plugin")
    implementation("org.codenarc:CodeNarc") {
        exclude(group = "org.codehaus.groovy")
    }
    implementation("com.github.javaparser:javaparser-symbol-solver-core")
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-conventions")
    implementation(kotlin("compiler-embeddable") as String) {
        because("Required by IncubatingApiReportTask")
    }
}
