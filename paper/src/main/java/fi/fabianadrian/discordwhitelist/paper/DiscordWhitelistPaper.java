package fi.fabianadrian.discordwhitelist.paper;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.Platform;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import fi.fabianadrian.discordwhitelist.paper.command.CommandSourceStackWrapper;
import fi.fabianadrian.discordwhitelist.paper.listener.JoinListener;
import fi.fabianadrian.discordwhitelist.paper.profile.OnlineProfileResolver;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class DiscordWhitelistPaper extends JavaPlugin implements Platform {
	private DiscordWhitelist discordWhitelist;
	private PaperCommandManager<Audience> commandManager;
	private ProfileResolver onlineProfileResolver;

	@Override
	public void onLoad() {
		this.discordWhitelist = new DiscordWhitelist(this);
	}

	@Override
	public void onEnable() {
		this.onlineProfileResolver = new OnlineProfileResolver(getServer());
		createCommandManager();
		try {
			this.discordWhitelist.load();
		} catch (SQLException ignored) {
		}
		registerListeners();
	}

	@Override
	public Path dataDirectory() {
		return getDataFolder().toPath();
	}

	@Override
	public Logger logger() {
		return getSLF4JLogger();
	}

	@Override
	public ProfileResolver onlineProfileResolver() {
		return this.onlineProfileResolver;
	}

	@Override
	public PaperCommandManager<Audience> commandManager() {
		return this.commandManager;
	}

	@Override
	public CompletableFuture<Stream<String>> onlinePlayerNames() {
		CompletableFuture<Stream<String>> future = new CompletableFuture<>();
		Bukkit.getScheduler().runTask(this, () -> future.complete(
				getServer().getOnlinePlayers().stream().map(Player::getName)
		));
		return future;
	}

	@Override
	public @NotNull Audience audience() {
		return getServer();
	}

	public DiscordWhitelist discordWhitelist() {
		return this.discordWhitelist;
	}

	private void createCommandManager() {
		SenderMapper<CommandSourceStack, Audience> mapper = SenderMapper.create(
				CommandSourceStackWrapper::new,
				audience -> ((CommandSourceStackWrapper) audience).stack()
		);
		PaperCommandManager<Audience> manager = PaperCommandManager.builder(mapper)
				.executionCoordinator(ExecutionCoordinator.simpleCoordinator())
				.buildOnEnable(this);

		this.commandManager = manager;
	}

	private void registerListeners() {
		PluginManager manager = getServer().getPluginManager();
		List.of(
				new JoinListener(this)
		).forEach(listener -> manager.registerEvents(listener, this));
	}
}
