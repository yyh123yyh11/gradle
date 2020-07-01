/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    `java-library`
    id("org.gradle.kotlin.kotlin-dsl")
    id("org.gradle.kotlin-dsl.ktlint-convention")
    `java-gradle-plugin`
    groovy
}

group = "gradlebuild"

applyGroovyProjectConventions()
applyKotlinProjectConventions()

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    "api"(platform("gradlebuild:build-platform"))
    implementation(gradleApi())
}

afterEvaluate {
    if (tasks.withType<ValidatePlugins>().isEmpty()) {
        val validatePlugins by tasks.registering(ValidatePlugins::class) {
            outputFile.set(project.reporting.baseDirectory.file("task-properties/report.txt"))

            val mainSourceSet = project.sourceSets.main.get()
            classes.setFrom(mainSourceSet.output.classesDirs)
            dependsOn(mainSourceSet.output)
            classpath.setFrom(mainSourceSet.runtimeClasspath)
        }
        tasks.check { dependsOn(validatePlugins) }
    }
}

tasks.withType<ValidatePlugins>().configureEach {
    failOnWarning.set(true)
    enableStricterValidation.set(true)
}

apply(from = "../../gradle/shared-with-buildSrc/code-quality-configuration.gradle.kts")

repositories {
    maven {
        name = "Gradle libs"
        url = uri("https://repo.gradle.org/gradle/libs")
        mavenContent {
            // This repository contains an older version which has been overwritten in Central
            excludeModule("com.google.j2objc", "j2objc-annotations")
        }
    }
    gradlePluginPortal()
    maven {
        name = "Gradle snapshot libs"
        url = uri("https://repo.gradle.org/gradle/libs-snapshots")
        mavenContent {
            // This repository contains an older version which has been overwritten in Central
            excludeModule("com.google.j2objc", "j2objc-annotations")
        }
    }
    maven {
        name = "kotlinx"
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
    maven {
        name = "kotlin-dev"
        url = uri("https://dl.bintray.com/kotlin/kotlin-dev")
    }
    maven {
        name = "kotlin-eap"
        url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

if ("CI" in System.getenv()) {
    println("Current machine has ${Runtime.getRuntime().availableProcessors()} CPU cores")

    if (OperatingSystem.current().isWindows) {
        require(Runtime.getRuntime().availableProcessors() >= 6) { "Windows CI machines must have at least 6 CPU cores!" }
    }
    gradle.buildFinished {
        allprojects.forEach { project ->
            project.tasks.all {
                if (this is Reporting<*> && state.failure != null) {
                    prepareReportForCIPublishing(project.name, this.reports["html"].destination)
                }
            }
        }
    }
}

fun prepareReportForCIPublishing(projectName: String, report: File) {
    if (report.isDirectory) {
        val destFile = File("${rootProject.buildDir}/report-$projectName-${report.name}.zip")
        ant.withGroovyBuilder {
            "zip"("destFile" to destFile) {
                "fileset"("dir" to report)
            }
        }
    } else {
        copy {
            from(report)
            into(rootProject.buildDir)
            rename { "report-$projectName-${report.parentFile.name}-${report.name}" }
        }
    }
}


fun applyGroovyProjectConventions() {

    dependencies {
        implementation(localGroovy())
        testImplementation("org.spockframework:spock-core:1.3-groovy-2.5") {
            exclude(group = "org.codehaus.groovy")
        }
        testImplementation("net.bytebuddy:byte-buddy:1.8.21")
        testImplementation("org.objenesis:objenesis:2.6")
    }

    tasks.withType<GroovyCompile>().configureEach {
        groovyOptions.apply {
            encoding = "utf-8"
            forkOptions.jvmArgs?.add("-XX:+HeapDumpOnOutOfMemoryError")
        }
        options.apply {
            isFork = true
            encoding = "utf-8"
            compilerArgs = mutableListOf("-Xlint:-options", "-Xlint:-path")
        }
        val vendor = System.getProperty("java.vendor")
        inputs.property("javaInstallation", "$vendor ${JavaVersion.current()}")
    }

    tasks.withType<Test>().configureEach {
        if (JavaVersion.current().isJava9Compatible) {
            //allow ProjectBuilder to inject legacy types into the system classloader
            jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
            jvmArgs("--illegal-access=deny")
        }
    }

    val compileGroovy by tasks.existing(GroovyCompile::class)

    configurations {
        apiElements {
            outgoing.variants["classes"].artifact(
                mapOf(
                    "file" to compileGroovy.get().destinationDir,
                    "type" to ArtifactTypeDefinition.JVM_CLASS_DIRECTORY,
                    "builtBy" to compileGroovy
                ))
        }
    }
}

fun applyKotlinProjectConventions() {
    plugins.withType<KotlinDslPlugin> {
        configure<KotlinDslPluginOptions> {
            experimentalWarning.set(false)
        }
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        // TODO:kotlin-dsl remove precompiled script plugins accessors exclusion from ktlint checks
        filter {
            exclude("gradle/kotlin/dsl/accessors/_*/**")
        }
    }
}

