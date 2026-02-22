package fi.fabianadrian.discordwhitelist.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.Platform;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import fi.fabianadrian.discordwhitelist.velocity.listener.LoginListener;
import fi.fabianadrian.discordwhitelist.velocity.profile.OnlineProfileResolver;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.CloudInjectionModule;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public final class DiscordWhitelistVelocity implements Platform {
	private final ProxyServer server;
	private final Logger logger;
	private final Path dataDirectory;
	private final Injector injector;
	private final DiscordWhitelist discordWhitelist;
	private final ProfileResolver onlineProfileResolver;
	private VelocityCommandManager<Audience> commandManager;

	@Inject
	public DiscordWhitelistVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Injector injector) {
		this.server = server;
		this.logger = logger;
		this.dataDirectory = dataDirectory;
		this.injector = injector;

		this.onlineProfileResolver = new OnlineProfileResolver(server);

		createCommandManager();

		this.discordWhitelist = new DiscordWhitelist(this);
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		try {
			this.discordWhitelist.load();
		} catch (SQLException ignored) {
		}

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
	public ProfileResolver onlineProfileResolver() {
		return this.onlineProfileResolver;
	}

	@Override
	public VelocityCommandManager<Audience> commandManager() {
		return this.commandManager;
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

	private void createCommandManager() {
		SenderMapper<CommandSource, Audience> mapper = SenderMapper.create(
				source -> source,
				audience -> (CommandSource) audience
		);

		Injector childInjector = this.injector.createChildInjector(
				new CloudInjectionModule<>(
						Audience.class,
						ExecutionCoordinator.simpleCoordinator(),
						mapper
				)
		);

		this.commandManager = childInjector.getInstance(Key.get(new TypeLiteral<>() {
		}));
	}

	@Override
	public @NotNull Audience audience() {
		return this.server;
	}
}
