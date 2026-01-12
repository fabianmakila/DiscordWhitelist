package fi.fabianadrian.discordwhitelist.common.command.discord;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.discord.jda6.JDAInteraction;
import org.incendo.cloud.translations.TranslationBundle;
import org.jetbrains.annotations.NotNull;


public final class DiscordCaptionProvider implements CaptionProvider<JDAInteraction> {
	private final TranslationBundle<JDAInteraction> bundle;

	public DiscordCaptionProvider(DiscordWhitelist discordWhitelist) {
		this.bundle = TranslationBundle.resourceBundle("messages", new DiscordLocaleExtractor(discordWhitelist));
	}

	@Override
	public @Nullable String provide(@NonNull Caption caption, @NotNull JDAInteraction recipient) {
		return bundle.provide(Caption.of("discordwhitelist.command.discord." + caption.key()), recipient);
	}
}
