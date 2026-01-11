package fi.fabianadrian.discordwhitelist.common.discord;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public final class DiscordBot {
	private final JDA jda;

	public DiscordBot(DiscordWhitelist discordWhitelist) {
		String token = discordWhitelist.config().token();
		this.jda = JDABuilder.createLight(token)
				.addEventListeners(discordWhitelist.discordCommandManager().createListener())
				.build();
	}
}
