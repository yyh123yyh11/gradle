gradlePlugin {
    plugins {
        register("settingsPlugin") {
            id = "gradlebuild.Settings"
            implementationClass = "gradlebuild.SettingsPlugin"
        }
    }
}
