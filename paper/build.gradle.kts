import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
	id("discordwhitelist.paper-conventions")
}

dependencies {
	compileOnly(libs.platform.paper)
	implementation(project(":common"))
}

paperPluginYaml {
	main = "fi.fabianadrian.discordwhitelist.paper.DiscordWhitelistPaper"
	name = prefixedPluginName
	author = "FabianAdrian"
	apiVersion = "1.21.11"
	dependencies {
		server {
			register("LuckPerms") {
				required = false
				load = PaperPluginYaml.Load.BEFORE
			}
		}
	}
}