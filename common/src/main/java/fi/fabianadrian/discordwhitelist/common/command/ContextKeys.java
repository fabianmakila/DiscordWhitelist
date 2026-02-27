package fi.fabianadrian.discordwhitelist.common.command;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import org.incendo.cloud.key.CloudKey;

public final class ContextKeys {
	public static final CloudKey<DiscordWhitelist> DISCORD_WHITELIST = CloudKey.of("DiscordWhitelist", DiscordWhitelist.class);

	private ContextKeys() {
	}
}
