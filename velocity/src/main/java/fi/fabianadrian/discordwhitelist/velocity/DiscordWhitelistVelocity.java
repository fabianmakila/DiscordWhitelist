package fi.fabianadrian.discordwhitelist.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.Platform;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ChainedProfileResolver;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.LuckPermsProfileResolver;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ProfileResolver;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.crafthead.CraftHeadProfileResolver;
import fi.fabianadrian.discordwhitelist.velocity.listener.LoginListener;
import fi.fabianadrian.discordwhitelist.velocity.profile.OnlineProfileResolver;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class DiscordWhitelistVelocity implements Platform {
	private final ProxyServer server;
	private final Logger logger;
	private final Path dataDirectory;
	private final DiscordWhitelist discordWhitelist;
	private ChainedProfileResolver profileResolver;

	@Inject
	public DiscordWhitelistVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
		this.server = server;
		this.logger = logger;
		this.dataDirectory = dataDirectory;

		this.discordWhitelist = new DiscordWhitelist(this);
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		List<ProfileResolver> resolvers = new ArrayList<>();
		resolvers.add(new OnlineProfileResolver(this.server));

		if (this.server.getPluginManager().isLoaded("luckperms")) {
			resolvers.add(new LuckPermsProfileResolver());
		}

		resolvers.add(new CraftHeadProfileResolver());
		this.profileResolver = new ChainedProfileResolver(this.logger, resolvers);

		this.discordWhitelist.load();

		registerListeners();
	}

	@Override
	public Path dataDirectory() {
		return this.dataDirectory;
	}

	@Override
	public Logger logger() {
		return this.logger;
	}

	@Override
	public ChainedProfileResolver profileResolver() {
		return this.profileResolver;
	}

	public DiscordWhitelist discordWhitelist() {
		return this.discordWhitelist;
	}

	private void registerListeners() {
		EventManager manager = this.server.getEventManager();
		List.of(
				new LoginListener(this)
		).forEach(event -> manager.register(this, event));
	}
}
