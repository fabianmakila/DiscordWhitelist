plugins {
    id("discordwhitelist.java-conventions")
}

dependencies {
    compileOnly(libs.slf4j)
    compileOnly(libs.storage.sqlite)
    compileOnly(libs.adventure)
    compileOnly(libs.gson)
    compileOnly(libs.luckperms)
    implementation(libs.cloud.jda)
    implementation(libs.cloud.translations)
    implementation(libs.jda) {
        exclude(module="opus-java")
        exclude(module="tink")
    }
    implementation(libs.dazzleconf)
}