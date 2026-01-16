package fi.fabianadrian.discordwhitelist.common.config;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.config.liaison.LocaleLiaison;
import space.arim.dazzleconf.Configuration;
import space.arim.dazzleconf.StandardErrorPrint;
import space.arim.dazzleconf.backend.Backend;
import space.arim.dazzleconf.backend.PathRoot;
import space.arim.dazzleconf.backend.toml.TomlBackend;

public final class ConfigManager {
	private final Configuration<DiscordWhitelistConfig> configuration;
	private final Backend backend;
	private final StandardErrorPrint errorPrint;
	private DiscordWhitelistConfig config;

	public ConfigManager(DiscordWhitelist discordWhitelist) {
		this.configuration = Configuration.defaultBuilder(DiscordWhitelistConfig.class).addTypeLiaisons(new LocaleLiaison()).build();
		this.backend = new TomlBackend(new PathRoot(discordWhitelist.dataDirectory().resolve("config.toml")));
		this.errorPrint = new StandardErrorPrint(output -> discordWhitelist.logger().error(output.printString()));
	}

	public void load() {
		this.config = this.configuration.configureOrFallback(this.backend, this.errorPrint);
	}

	public DiscordWhitelistConfig config() {
		return this.config;
	}
}
