package fi.fabianadrian.discordwhitelist.common.command.discord.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.discord.DiscordCommand;
import fi.fabianadrian.discordwhitelist.common.command.parser.MinecraftProfileParser;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.data.DataManager;
import fi.fabianadrian.discordwhitelist.common.profile.DiscordProfile;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda6.JDAInteraction;

import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

public final class LinkCommand extends DiscordCommand {
	private final DataManager dataManager;

	public LinkCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist);
		this.dataManager = discordWhitelist.storageManager();
	}

	@Override
	public void register() {
		var builder = super.manager.commandBuilder("link")
				.required("minecraft-username", MinecraftProfileParser.minecraftProfileParser())
				.handler(this::handle);
		super.manager.command(builder);
	}

	private void handle(CommandContext<JDAInteraction> context) {
		GenericCommandInteractionEvent event = context.sender().interactionEvent();
		assert event != null;

		User user = event.getUser();
		MinecraftProfile minecraftProfile = context.get("minecraft-username");

		this.dataManager.findByDiscordIdentifier(user.getIdLong())
				.thenCompose(collection -> {
					if (!collection.isEmpty() && collection.size() >= super.discordWhitelist.config().profileLimit()) {
						StringJoiner builder = new StringJoiner(", ");
						collection.forEach(profile -> builder.add(profile.minecraftProfile().username()));
						sendMessage(
								context,
								"link.error.limit",
								CaptionVariable.of("linked-minecraft-usernames", builder.toString())
						);
						return CompletableFuture.completedFuture(null);
					}

					return this.dataManager.findByMinecraftIdentifier(minecraftProfile.identifier())
							.thenCompose(data -> {
								if (data != null && data.discordProfile() != null) {
									sendMessage(
											context,
											"link.error.other",
											CaptionVariable.of("discord-username", data.discordProfile().username()),
											CaptionVariable.of("minecraft-username", minecraftProfile.username())
									);
									return CompletableFuture.completedFuture(null);
								}
								data = new Data(minecraftProfile);
								data.discordProfile(new DiscordProfile(user));
								return this.dataManager.save(data)
										.thenRun(() -> {
											sendMessage(
													context,
													"link",
													CaptionVariable.of("minecraft-username", minecraftProfile.username())
											);
											//TODO Broadcast
										});
							});
				}).exceptionally(throwable -> {
					super.discordWhitelist.logger().error("Couldn't link Discord account", throwable);
					sendMessage(context, "link.error.unknown");
					return null;
				});
	}
}
