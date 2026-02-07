package fi.fabianadrian.discordwhitelist.common.discord;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public final class DiscordBot {
	private TicketFactory ticketFactory;

	public DiscordBot(DiscordWhitelist discordWhitelist) {
		String token = discordWhitelist.config().token();

		if (token.isEmpty()) {
			discordWhitelist.logger().warn("Bot token is empty! Discord commands won't be registered");
			return;
		}

		final JDA jda = JDABuilder.createLight(token)
				.addEventListeners(discordWhitelist.discordCommandManager().createListener())
				.build();
		this.ticketFactory = new TicketFactory(discordWhitelist, jda);
	}

	public TicketFactory ticketFactory() {
		return this.ticketFactory;
	}
}
