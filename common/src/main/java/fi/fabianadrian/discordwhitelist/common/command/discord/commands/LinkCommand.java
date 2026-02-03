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

import java.util.Collection;

public final class LinkCommand extends DiscordCommand {
	private static final TranslatableComponent COMPONENT_BROADCAST = Component.translatable(
			"discordwhitelist.command.discord.link.broadcast"
	);
	private final DataManager dataManager;

	public LinkCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "link");
		this.dataManager = discordWhitelist.dataManager();
	}

	@Override
	public void register() {
		super.manager.command(super.builder
				.required("minecraft-username", MinecraftProfileParser.minecraftProfileParser())
				.handler(this::handle)
		);
	}

	private void handle(CommandContext<JDAInteraction> context) {
		GenericCommandInteractionEvent event = context.sender().interactionEvent();
		if (event == null) {
			super.discordWhitelist.logger().warn("GenericCommandInteractionEvent was null");
			return;
		}

		User user = event.getUser();
		MinecraftProfile minecraftProfile = context.get("minecraft-username");

		Data dataByMinecraftIdentifier = this.dataManager.findByMinecraftIdentifier(minecraftProfile.identifier()).join();
		if (dataByMinecraftIdentifier != null && dataByMinecraftIdentifier.discordProfile() != null) {
			DiscordProfile discordProfile = dataByMinecraftIdentifier.discordProfile();
			if (discordProfile.identifier().equals(user.getIdLong())) {
				sendMessage(
						context,
						"discord.link.already-linked.self",
						CaptionVariable.of("minecraft-username", minecraftProfile.username())
				);
			} else {
				sendMessage(
						context,
						"discord.link.already-linked.other",
						CaptionVariable.of("discord-identifier", dataByMinecraftIdentifier.discordProfile().identifier().toString()),
						CaptionVariable.of("minecraft-username", minecraftProfile.username())
				);
			}
			return;
		}

		Collection<Data> dataByDiscordIdentifier = this.dataManager.findByDiscordIdentifier(user.getIdLong()).join();
		int profileLimit = super.discordWhitelist.config().profileLimit();
		if (dataByDiscordIdentifier.size() >= profileLimit) {
			sendMessage(
					context,
					"discord.link.limit-reached",
					CaptionVariable.of("limit", String.valueOf(profileLimit))
			);
			return;
		}

		Data data = new Data(minecraftProfile);
		data.discordProfile(new DiscordProfile(user));
		this.dataManager.save(data).join();

		sendMessage(
				context,
				"discord.link",
				CaptionVariable.of("minecraft-username", minecraftProfile.username())
		);
		super.discordWhitelist.broadcast(
				"discordwhitelist.command.discord.link.broadcast",
				COMPONENT_BROADCAST.arguments(
						Argument.string("discord-username", data.discordProfile().username()),
						Argument.string("minecraft-username", minecraftProfile.username())
				)
		);
	}
}
