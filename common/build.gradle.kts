plugins {
    id("discordwhitelist.java-conventions")
}

configurations {
    configureEach {
        exclude(module="tink")
        exclude(module="opus-java")
        exclude(module="cloud-annotations")
    }
}

dependencies {
    compileOnly(libs.adventure)
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.gson)
    compileOnly(libs.luckperms)
    compileOnly(libs.slf4j)
    compileOnly(libs.storage.mariadb)
    compileOnly(libs.storage.sqlite)
    implementation(libs.cloud.jda) {
        exclude(module="slf4j-api")
        exclude(group="org.jetbrains")
    }
    implementation(libs.cloud.translations)
    implementation(libs.dazzleconf)
    implementation(libs.hikaricp) {
        exclude(module="slf4j-api")
    }
    implementation(libs.jda) {
        exclude(module="slf4j-api")
        exclude(group="org.jetbrains")
    }
    implementation(libs.flyway.mysql)
    implementation(libs.flyway.core)
}