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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda6.JDAInteraction;

import java.util.concurrent.CompletableFuture;

//TODO Swap order check if minecraft account exists -> check if already linked that account -> check if user has reached limit
public final class LinkCommand extends DiscordCommand {
	private static final TranslatableComponent COMPONENT_BROADCAST = Component.translatable(
			"discordwhitelist.command.discord.link.broadcast"
	);
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

		this.dataManager.findByDiscordIdentifier(user.getIdLong())
				.thenCompose(collection -> {
					int profileLimit = super.discordWhitelist.config().profileLimit();
					if (!collection.isEmpty() && collection.size() >= profileLimit) {
						sendMessage(
								context,
								"discord.link.error.limit",
								CaptionVariable.of("limit", String.valueOf(profileLimit))
						);
						return CompletableFuture.completedFuture(null);
					}

					MinecraftProfile minecraftProfile = context.get("minecraft-username");

					return this.dataManager.findByMinecraftIdentifier(minecraftProfile.identifier())
							.thenCompose(data -> {
								if (data != null && data.discordProfile() != null) {
									sendMessage(
											context,
											"discord.link.error.other",
											CaptionVariable.of("discord-username", data.discordProfile().username()),
											CaptionVariable.of("minecraft-username", minecraftProfile.username())
									);
									return CompletableFuture.completedFuture(null);
								}
								data = new Data(minecraftProfile);
								data.discordProfile(new DiscordProfile(user));
								String discordUsername = data.discordProfile().username();
								return this.dataManager.save(data)
										.thenRun(() -> {
											sendMessage(
													context,
													"link",
													CaptionVariable.of("minecraft-username", minecraftProfile.username())
											);
											super.discordWhitelist.serverAudience().sendMessage(
													COMPONENT_BROADCAST.arguments(
															Argument.string("discord-username", discordUsername),
															Argument.string("minecraft-username", minecraftProfile.username())
													)
											);
										});
							});
				}).exceptionally(throwable -> {
					super.discordWhitelist.logger().error("Couldn't link Discord account", throwable);
					sendMessage(context, "discord.link.error.unknown");
					return null;
				});
	}
}
