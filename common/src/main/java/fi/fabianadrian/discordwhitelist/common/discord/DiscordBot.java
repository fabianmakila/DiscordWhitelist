package fi.fabianadrian.discordwhitelist.common.discord;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import net.dv8tion.jda.api.JDABuilder;

public final class DiscordBot {

	public DiscordBot(DiscordWhitelist discordWhitelist) {
		String token = discordWhitelist.config().token();

		if (token.isEmpty()) {
			discordWhitelist.logger().warn("Bot token is empty! Discord commands won't be registered");
			return;
		}

		JDABuilder.createLight(token)
				.addEventListeners(discordWhitelist.discordCommandManager().createListener())
				.build();
	}
}
