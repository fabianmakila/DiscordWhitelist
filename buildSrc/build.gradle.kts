plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.2.1")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.4.0")
    implementation("xyz.jpenilla.resource-factory-paper-convention:xyz.jpenilla.resource-factory-paper-convention.gradle.plugin:1.3.1")
    implementation("xyz.jpenilla.resource-factory-velocity-convention:xyz.jpenilla.resource-factory-velocity-convention.gradle.plugin:1.3.1")
}