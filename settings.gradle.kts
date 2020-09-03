import org.gradle.api.internal.FeaturePreviews

/*
 * Copyright 2010 the original author or authors.
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

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
    }
}

plugins {
    id("com.gradle.enterprise").version("3.4.1")
    id("com.gradle.enterprise.gradle-enterprise-conventions-plugin").version("0.7.1")
}

apply(from = "gradle/shared-with-buildSrc/mirrors.settings.gradle.kts")

rootProject.name = "gradle"
fixedGroup("org.gradle")

mainBuild("main-build")



fun mainBuild(folder : String) {
    val mainFolder = file(folder)
    includeAll(mainFolder, mainFolder)
}

fun includeAll(mainFolder : File, subFolder: File) {
    subFolder.listFiles()!!.filter { it.isDirectory }.forEach { dir ->
        if (File(dir, "build.gradle").exists() || File(dir, "build.gradle.kts").exists()) {
            subproject(dir, ":" + dir.relativeTo(mainFolder).toString().replace(File.separator, ":"))
        } else {
            includeAll(mainFolder, dir)
        }
    }
}

fun subproject(folder: File, projectPath: String) {
    include(projectPath)
    project(projectPath).projectDir = folder

    // TODO deactivate intermediate parent projects (which is not supported currently)
}

FeaturePreviews.Feature.values().forEach { feature ->
    if (feature.isActive) {
        enableFeaturePreview(feature.name)
    }
}
