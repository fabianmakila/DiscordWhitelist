plugins {
	id("discordwhitelist.java-conventions")
	id("com.gradleup.shadow")
}

tasks {
	build {
		dependsOn(shadowJar)
	}
	shadowJar {
		archiveBaseName.set(project.prefixedPluginName)
		destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
		archiveClassifier.set("")
		filesMatching("META-INF/services/**") {
			duplicatesStrategy = DuplicatesStrategy.INCLUDE
		}
	}
}