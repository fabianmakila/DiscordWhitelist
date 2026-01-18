package fi.fabianadrian.discordwhitelist.paper;

import fi.fabianadrian.discordwhitelist.common.Platform;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ChainedProfileResolver;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.LuckPermsProfileResolver;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ProfileResolver;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.crafthead.CraftHeadProfileResolver;
import fi.fabianadrian.discordwhitelist.paper.profile.OnlineProfileResolver;
import net.kyori.adventure.audience.Audience;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class DiscordWhitelistPaper extends JavaPlugin implements Platform {
	private ChainedProfileResolver profileResolver;

	@Override
	public void onEnable() {
		List<ProfileResolver> resolvers = new ArrayList<>();
		resolvers.add(new OnlineProfileResolver(getServer()));

		PluginManager manager = getServer().getPluginManager();
		if (manager.isPluginEnabled("LuckPerms")) {
			resolvers.add(new LuckPermsProfileResolver());
		}

		resolvers.add(new CraftHeadProfileResolver());
		this.profileResolver = new ChainedProfileResolver(getSLF4JLogger(), resolvers);
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
	public ChainedProfileResolver profileResolver() {
		return this.profileResolver;
	}

	@Override
	public PaperCommandManager<Audience> commandManager() {
		return null; //TODO Implementation
	}

	@Override
	public @NotNull Audience audience() {
		return getServer();
	}
}
