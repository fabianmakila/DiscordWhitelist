package fi.fabianadrian.discordwhitelist.common.command.minecraft.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.minecraft.MinecraftCommand;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.UUIDParser;

import java.util.UUID;

public final class LookupCommand extends MinecraftCommand {
	private static final String PERMISSION = "discordwhitelist.command.lookup";

	public LookupCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "lookup");
	}

	@Override
	public void register() {
		Command.Builder<Audience> builder = super.builder().permission(PERMISSION);

		Command.Builder<Audience> minecraftBuilder = builder.literal("minecraft");
		super.manager.command(minecraftBuilder
				.literal("identifier")
				.required("identifier", UUIDParser.uuidParser())
				.handler(this::handleMinecraftIdentifier)
		);
	}

	private void handleMinecraftIdentifier(CommandContext<Audience> context) {
		UUID uuid = context.get("identifier");
		super.discordWhitelist.dataManager().findByMinecraftIdentifier(uuid).thenAccept(data -> {
			if (data == null) {
				//TODO Message
				return;
			}
			context.sender().sendMessage(dataToComponent(data));
		});
	}

	private void handleMinecraftUsername(CommandContext<Audience> context) {

	}

	private void handleDiscordIdentifier(CommandContext<Audience> context) {

	}

	private void handleDiscordUsername(CommandContext<Audience> context) {

	}

	private Component dataToComponent(Data data) {
		return Component.join(
				JoinConfiguration.newlines(),
				Component.translatable(
						"discordwhitelist.command.minecraft.lookup.discord.identifier",
						Argument.string("identifier", String.valueOf(data.discordProfile().identifier()))
				),
				Component.translatable(
						"discordwhitelist.command.minecraft.lookup.discord.username",
						Argument.string("username", data.discordProfile().username())
				),
				Component.translatable(
						"discordwhitelist.command.minecraft.lookup.minecraft.identifier",
						Argument.string("identifier", String.valueOf(data.minecraftProfile().identifier()))
				),
				Component.translatable(
						"discordwhitelist.command.minecraft.lookup.minecraft.username",
						Argument.string("username", data.minecraftProfile().username())
				)
		);
	}
}
