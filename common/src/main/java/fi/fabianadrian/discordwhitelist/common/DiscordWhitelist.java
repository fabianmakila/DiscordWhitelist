package fi.fabianadrian.discordwhitelist.common;

import fi.fabianadrian.discordwhitelist.common.command.AbstractCommand;
import fi.fabianadrian.discordwhitelist.common.command.discord.DiscordCaptionProvider;
import fi.fabianadrian.discordwhitelist.common.command.discord.commands.LinkCommand;
import fi.fabianadrian.discordwhitelist.common.command.processor.DiscordWhitelistPreprocessor;
import fi.fabianadrian.discordwhitelist.common.config.ConfigManager;
import fi.fabianadrian.discordwhitelist.common.config.DiscordWhitelistConfig;
import fi.fabianadrian.discordwhitelist.common.data.DataManager;
import fi.fabianadrian.discordwhitelist.common.discord.DiscordBot;
import fi.fabianadrian.discordwhitelist.common.locale.TranslationManager;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ChainedProfileResolver;
import net.kyori.adventure.key.Key;
import org.incendo.cloud.discord.jda6.JDA6CommandManager;
import org.incendo.cloud.discord.jda6.JDAInteraction;
import org.incendo.cloud.discord.slash.DiscordSetting;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;

public final class DiscordWhitelist {
	private final Platform platform;
	private final ConfigManager configManager;
	private final TranslationManager translationManager;
	private final DataManager dataManager;
	private DiscordBot discordBot;
	private JDA6CommandManager<JDAInteraction> discordCommandManager;

	public DiscordWhitelist(Platform platform) {
		this.platform = platform;

		this.translationManager = new TranslationManager(
				logger(),
				dataDirectory().resolve("translations"),
				Key.key("discordwhitelist", "main")
		);

		this.configManager = new ConfigManager(this);
		this.dataManager = new DataManager(this);

		createDiscordCommandManager();
	}

	public void load() {
		this.configManager.load();

		this.translationManager.load();
		this.translationManager.defaultLocale(config().defaultLocale());

		this.dataManager.load();

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
		JDA6CommandManager<JDAInteraction> commandManager = new JDA6CommandManager<>(
				ExecutionCoordinator.simpleCoordinator(),
				JDAInteraction.InteractionMapper.identity()
		);

		// Translations
		commandManager.captionRegistry().registerProvider(new DiscordCaptionProvider(this));

		commandManager.discordSettings().set(DiscordSetting.FORCE_DEFER_EPHEMERAL, true);
		commandManager.discordSettings().set(DiscordSetting.EPHEMERAL_ERROR_MESSAGES, true);

		commandManager.registerCommandPreProcessor(new DiscordWhitelistPreprocessor<>(this));

		this.discordCommandManager = commandManager;
	}

	private void registerDiscordCommands() {
		List.of(
				new LinkCommand(this)
		).forEach(AbstractCommand::register);
	}
}
