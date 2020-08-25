package Gradle_Util_Performance.buildTypes

import common.Os
import common.applyPerformanceTestSettings
import common.buildToolGradleParameters
import common.builtInRemoteBuildCacheNode
import common.checkCleanM2
import common.gradleWrapper
import common.individualPerformanceTestArtifactRules
import common.performanceTestCommandLine
import configurations.individualPerformanceTestJavaHome
import configurations.killAllGradleProcessesWindows
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.ParameterDisplay
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object AdHocPerformanceScenarioWindows : BuildType({
    uuid = "AdHocPerformanceScenarioWindows"
    name = "AdHoc Performance Scenario - Windows"
    id("Gradle_Util_Performance_AdHocPerformanceScenarioWindows")

    applyPerformanceTestSettings(timeout = 420)
    artifactRules = individualPerformanceTestArtifactRules

    params {
        text("baselines", "defaults", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("templates", "", display = ParameterDisplay.PROMPT, allowEmpty = false)
        param("channel", "adhoc-windows")
        param("checks", "all")
        text("runs", "10", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("warmups", "3", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("scenario", "", display = ParameterDisplay.PROMPT, allowEmpty = false)
        param("flamegraphs", "--flamegraphs false")
        param("additional.gradle.parameters", "")

        param("env.ANDROID_HOME", Os.windows.androidHome)
    }

    steps {
        script {
            name = "KILL_GRADLE_PROCESSES"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = killAllGradleProcessesWindows
        }
        gradleWrapper {
            name = "GRADLE_RUNNER"
            gradleParams = (
                performanceTestCommandLine(
                    "clean %templates% performance:performanceAdHocTest",
                    "%baselines%",
                    """--scenarios "%scenario%" --warmups %warmups% --runs %runs% --checks %checks% --channel %channel% %flamegraphs% %additional.gradle.parameters%""",
                    individualPerformanceTestJavaHome(Os.windows)
                ) +
                    buildToolGradleParameters(isContinue = false) +
                    builtInRemoteBuildCacheNode.gradleParameters(Os.windows)
                ).joinToString(separator = " ")
        }
        checkCleanM2()
    }
})
