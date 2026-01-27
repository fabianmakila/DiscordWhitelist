package fi.fabianadrian.discordwhitelist.common.config;

import fi.fabianadrian.discordwhitelist.common.config.section.StorageSection;
import fi.fabianadrian.discordwhitelist.common.config.section.TicketsSection;
import space.arim.dazzleconf.engine.Comments;
import space.arim.dazzleconf.engine.liaison.SubSection;

import java.util.Locale;

public interface DiscordWhitelistConfig {
	@Comments("Discord bot token")
	default String token() {
		return "";
	}

	@Comments("How many profiles Discord users are allowed to link")
	default int profileLimit() {
		return 1;
	}

	default Locale defaultLocale() {
		return Locale.ENGLISH;
	}

	@SubSection
	StorageSection storage();

	@SubSection
	TicketsSection tickets();
}
