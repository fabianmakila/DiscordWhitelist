plugins {
	id("discordwhitelist.shadow-conventions")
	id("xyz.jpenilla.resource-factory-velocity-convention")
}

velocityPluginJson {
	name = prefixedPluginName
	authors.add("FabianAdrian")
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
			"kotlin"
		).forEach {
			relocate(it, "fi.fabianadrian.discordwhitelist.dependency.$it")
		}
	}
}