import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

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
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
		transform(ServiceFileTransformer::class.java) {
			include("META-INF/services/*")
		}
	}
}