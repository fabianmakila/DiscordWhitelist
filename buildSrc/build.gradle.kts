plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.9.0")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.6.1")
    implementation("xyz.jpenilla.resource-factory-paper-convention:xyz.jpenilla.resource-factory-paper-convention.gradle.plugin:1.3.1")
    implementation("xyz.jpenilla.resource-factory-velocity-convention:xyz.jpenilla.resource-factory-velocity-convention.gradle.plugin:1.3.1")
}