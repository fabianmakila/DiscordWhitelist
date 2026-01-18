package fi.fabianadrian.discordwhitelist.common.command.discord;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.discord.jda6.JDAInteraction;
import org.incendo.cloud.translations.LocaleExtractor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class DiscordLocaleExtractor implements LocaleExtractor<JDAInteraction> {
	private final DiscordWhitelist discordWhitelist;

	public DiscordLocaleExtractor(DiscordWhitelist discordWhitelist) {
		this.discordWhitelist = discordWhitelist;
	}

	@Override
	public @NonNull Locale extract(@NotNull JDAInteraction interaction) {
		var event = interaction.interactionEvent();
		if (event == null) {
			return this.discordWhitelist.config().defaultLocale();
		}
		return event.getUserLocale().toLocale();
	}
}
