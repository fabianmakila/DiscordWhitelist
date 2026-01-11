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

public final class LinkCommand extends DiscordCommand {
	private final DataManager dataManager;

	public LinkCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist);
		this.dataManager = discordWhitelist.storageManager();
	}

	@Override
	public void register() {
		var builder = super.manager.commandBuilder("link")
				.required("minecraftUsername", MinecraftProfileParser.minecraftProfileParser())
				.handler(this::handle);
		super.manager.command(builder);
	}

	private void handle(CommandContext<JDAInteraction> context) {
		GenericCommandInteractionEvent event = context.sender().interactionEvent();
		assert event != null;

		User user = event.getUser();
		this.dataManager.findByDiscordIdentifier(user.getIdLong())
				.thenAccept(collection -> {
					if (!collection.isEmpty() && collection.size() > super.discordWhitelist.config().profileLimit()) {
						StringBuilder builder = new StringBuilder(", ");
						collection.forEach(profile -> builder.append(profile.minecraftProfile().username()));
						sendMessage(
								context,
								"discord.link.error.limit",
								CaptionVariable.of("linked-minecraft-usernames", builder.toString())
						);
						return;
					}
					MinecraftProfile minecraftProfile = context.get("minecraft-username");
					this.dataManager.findByMinecraftIdentifier(minecraftProfile.identifier())
							.thenAccept(data -> {
								if (data != null && data.discordProfile() != null) {
									sendMessage(
											context,
											"discord.link.error.other",
											CaptionVariable.of("discord-username", data.discordProfile().username()),
											CaptionVariable.of("minecraft-username", minecraftProfile.username())
									);
									return;
								}
								data = new Data(minecraftProfile);
								data.discordProfile(new DiscordProfile(user));
								this.dataManager.save(data);

								sendMessage(
										context,
										"discord.link",
										CaptionVariable.of("minecraft-username", data.minecraftProfile().username())
								);
								//TODO Broadcast
							});
				});
	}
}
