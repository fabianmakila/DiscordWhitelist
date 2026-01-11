package fi.fabianadrian.discordwhitelist.common;

import fi.fabianadrian.discordwhitelist.common.command.AbstractCommand;
import fi.fabianadrian.discordwhitelist.common.command.discord.DiscordLocaleExtractor;
import fi.fabianadrian.discordwhitelist.common.command.discord.commands.LinkCommand;
import fi.fabianadrian.discordwhitelist.common.command.processor.DiscordWhitelistPreprocessor;
import fi.fabianadrian.discordwhitelist.common.config.ConfigManager;
import fi.fabianadrian.discordwhitelist.common.config.DiscordWhitelistConfig;
import fi.fabianadrian.discordwhitelist.common.data.DataManager;
import fi.fabianadrian.discordwhitelist.common.discord.DiscordBot;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ChainedProfileResolver;
import org.incendo.cloud.discord.jda6.JDA6CommandManager;
import org.incendo.cloud.discord.jda6.JDAInteraction;
import org.incendo.cloud.discord.slash.DiscordSetting;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.translations.TranslationBundle;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;

public final class DiscordWhitelist {
	private final Platform platform;
	private final DataManager dataManager;
	private final ConfigManager configManager;
	private final DiscordBot discordBot;
	private JDA6CommandManager<JDAInteraction> discordCommandManager;

	public DiscordWhitelist(Platform platform) {
		this.platform = platform;

		this.configManager = new ConfigManager(this);
		this.configManager.load();

		this.dataManager = new DataManager(this);
		this.dataManager.init();

		createDiscordCommandManager();
		registerDiscordCommands();

		this.discordBot = new DiscordBot(this);
	}

	public Path dataDirectory() {
		return this.platform.dataDirectory();
	}

	public Logger logger() {
		return this.platform.logger();
	}

	public ChainedProfileResolver profileResolver() {
		return this.platform.profileResolver();
	}

	public void reload() {
		this.configManager.load();
	}

	public DiscordWhitelistConfig config() {
		return this.configManager.config();
	}

	public JDA6CommandManager<JDAInteraction> discordCommandManager() {
		return this.discordCommandManager;
	}

	public DataManager storageManager() {
		return this.dataManager;
	}

	private void createDiscordCommandManager() {
		var commandManager = new JDA6CommandManager<>(
				ExecutionCoordinator.simpleCoordinator(),
				JDAInteraction.InteractionMapper.identity()
		);

		// Translations
		TranslationBundle<JDAInteraction> bundle = TranslationBundle.resourceBundle("messages", new DiscordLocaleExtractor(this));
		commandManager.captionRegistry().registerProvider(bundle);

		commandManager.discordSettings().set(DiscordSetting.FORCE_DEFER_EPHEMERAL, true);
		commandManager.discordSettings().set(DiscordSetting.EPHEMERAL_ERROR_MESSAGES, true);

		commandManager.registerCommandPreProcessor(new DiscordWhitelistPreprocessor<>(this));
	}

	private void registerDiscordCommands() {
		List.of(
				new LinkCommand(this)
		).forEach(AbstractCommand::register);
	}
}
