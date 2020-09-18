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

package gradlebuild

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.io.File

class SettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) = with(settings) {
        // repositories for plugins
        pluginManagement {
            repositories {
                gradlePluginPortal()
                maven {
                    setUrl("https://repo.gradle.org/gradle/libs-releases")
                }
                maven {
                    setUrl("https://repo.gradle.org/gradle/enterprise-libs-release-candidates-local")
                }
            }
        }

        // include all folders with a build script as subproject
        settingsDir.listFiles()!!.filter { it.hasBuildScript() }.forEach { subproject ->
            include(subproject.name)
        }
    }

    private fun File.hasBuildScript() = File(this, "build.gradle.kts").exists() || File(this, "build.gradle").exists()
}

