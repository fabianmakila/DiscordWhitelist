package fi.fabianadrian.discordwhitelist.common.discord;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class DiscordBot {
	private final DiscordWhitelist discordWhitelist;
	private JDA jda;

	public DiscordBot(DiscordWhitelist discordWhitelist) {
		this.discordWhitelist = discordWhitelist;

		String token = discordWhitelist.config().token();

		if (token.isEmpty()) {
			discordWhitelist.logger().warn("Bot token is empty! Discord commands won't be registered");
			return;
		}

		this.jda = JDABuilder.createLight(token)
				.addEventListeners(discordWhitelist.discordCommandManager().createListener())
				.build();
	}

	public CompletableFuture<PermissionOverride> createTicketChannel(User user) {
		Category ticketCategory = this.jda.getCategoryById(this.discordWhitelist.config().tickets().categoryIdentifier());
		if (ticketCategory == null) {
			return CompletableFuture.completedFuture(null);
		}
		return ticketCategory.createTextChannel(String.format("ticket-%s", toChannelName(user.getName()))).submit()
				.thenCompose(channel -> {
					Guild guild = channel.getGuild();
					Member member = guild.getMember(user);
					if (member == null) {
						return CompletableFuture.completedFuture(null);
					}
					return channel.upsertPermissionOverride(member).setAllowed(Permission.VIEW_CHANNEL).submit();
				});
	}

	private String toChannelName(String input) {
		String name = input.toLowerCase(Locale.ROOT);
		name = name.replaceAll("[^a-z0-9-]", "");
		return name.isBlank() ? "unknown" : name;
	}
}
