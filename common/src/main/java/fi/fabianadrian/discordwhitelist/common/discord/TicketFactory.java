package fi.fabianadrian.discordwhitelist.common.discord;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class TicketFactory {
	private final JDA jda;
	private final DiscordWhitelist discordWhitelist;

	public TicketFactory(DiscordWhitelist discordWhitelist, JDA jda) {
		this.discordWhitelist = discordWhitelist;
		this.jda = jda;
	}

	public CompletableFuture<TicketCreationResult> create(Member member) {
		Category ticketCategory = this.jda.getCategoryById(this.discordWhitelist.config().tickets().categoryIdentifier());
		if (ticketCategory == null) {
			return CompletableFuture.completedFuture(TicketCreationResult.MISSING_CATEGORY);
		}

		return createChannel(ticketCategory, member)
				.thenCompose(channel -> updateDescription(channel, member))
				.thenCompose(channel -> addPermissionOverride(channel, member))
				.thenApply(channel -> TicketCreationResult.SUCCESS);
	}

	private CompletableFuture<TextChannel> createChannel(Category ticketCategory, Member member) {
		return ticketCategory.createTextChannel("ticket-" + toChannelName(member.getUser().getName()))
				.submit();
	}

	private CompletableFuture<TextChannel> updateDescription(TextChannel channel, Member member) {
		return this.discordWhitelist.dataManager().findByDiscordIdentifier(member.getUser().getIdLong())
				.thenCompose(collection -> {
					String topic = collection.stream().map(data -> data.minecraftProfile().username()).collect(Collectors.joining(", "));
					return channel.getManager().setTopic(topic).submit().thenApply(ignored -> channel);
				});
	}

	private CompletableFuture<TextChannel> addPermissionOverride(TextChannel channel, Member member) {
		return channel.upsertPermissionOverride(member)
				.setAllowed(Permission.VIEW_CHANNEL)
				.submit()
				.thenApply(ignored -> channel);
	}

	private String toChannelName(String input) {
		String name = input.toLowerCase(Locale.ROOT);
		name = name.replaceAll("[^a-z0-9-]", "");
		return name.isBlank() ? "unknown" : name;
	}
}
