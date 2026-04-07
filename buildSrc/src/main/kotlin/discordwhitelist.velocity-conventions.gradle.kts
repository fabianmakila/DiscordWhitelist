plugins {
	id("discordwhitelist.shadow-conventions")
	id("xyz.jpenilla.resource-factory-velocity-convention")
}

tasks {
	shadowJar {
		listOf(
			"space.arim.dazzleconf",
			"com.zaxxer.hikari",
			"io.leangen.geantyref",
			"org.incendo.cloud",
			"org.apache.commons",
			"com.fasterxml.jackson",
			"com.neovisionaries.ws",
			"gnu.trove",
			"net.dv8tion.jda",
			"okhttp3",
			"okio",
			"kotlin",
			"org.flywaydb",
			"org.mariadb",
		).forEach {
			relocate(it, "fi.fabianadrian.discordwhitelist.dependency.$it")
		}
		mergeServiceFiles()
	}
}