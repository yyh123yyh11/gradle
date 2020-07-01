plugins {
    id("buildlogic-conventions")
}

dependencies {
    compileOnly("com.gradle:gradle-enterprise-gradle-plugin")

    implementation("gradlebuild:basics")
    implementation("gradlebuild:documentation")

    implementation("me.champeau.gradle:jmh-gradle-plugin")
    implementation("org.jsoup:jsoup")
}
