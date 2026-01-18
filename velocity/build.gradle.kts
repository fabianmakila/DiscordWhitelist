plugins {
	id("discordwhitelist.velocity-conventions")
}

dependencies {
	compileOnly(libs.platform.velocity)
	implementation(project(":common"))
	implementation(libs.storage.sqlite)
	implementation(libs.cloud.velocity)
}

velocityPluginJson {
	main = "fi.fabianadrian.discordwhitelist.velocity.DiscordWhitelistVelocity"
	id = "discordwhitelist"
	dependencies {
		dependency("luckperms", optional = true)
	}
}