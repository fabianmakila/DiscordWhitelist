package fi.fabianadrian.discordwhitelist.common.config;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.config.liaison.LocaleLiaison;
import org.slf4j.Logger;
import space.arim.dazzleconf.Configuration;
import space.arim.dazzleconf.StandardErrorPrint;
import space.arim.dazzleconf.backend.Backend;
import space.arim.dazzleconf.backend.PathRoot;
import space.arim.dazzleconf.backend.toml.TomlBackend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
	private final Configuration<DiscordWhitelistConfig> configuration;
	private final Backend backend;
	private final StandardErrorPrint errorPrint;
	private DiscordWhitelistConfig config;
	private final Path dataDirectory;
	private final Logger logger;

	public ConfigManager(DiscordWhitelist discordWhitelist) {
		this.dataDirectory = discordWhitelist.dataDirectory();
		this.logger = discordWhitelist.logger();

		this.configuration = Configuration.defaultBuilder(DiscordWhitelistConfig.class).addTypeLiaisons(new LocaleLiaison()).build();
		this.backend = new TomlBackend(new PathRoot(this.dataDirectory.resolve("config.toml")));
		this.errorPrint = new StandardErrorPrint(output -> this.logger.error(output.printString()));
	}

	public void load() {
		try {
			Files.createDirectories(this.dataDirectory);
		} catch (IOException e) {
			this.logger.error("Failed to create dataDirectory", e);
		}
		this.config = this.configuration.configureOrFallback(this.backend, this.errorPrint);
	}

	public DiscordWhitelistConfig config() {
		return this.config;
	}
}
