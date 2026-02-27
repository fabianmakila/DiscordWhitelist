package fi.fabianadrian.discordwhitelist.common.command.minecraft.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.minecraft.MinecraftCommand;
import fi.fabianadrian.discordwhitelist.common.command.parser.DataParser;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.standard.LongParser;
import org.incendo.cloud.parser.standard.StringParser;

public final class LookupCommand extends MinecraftCommand {
	private static final String PERMISSION = "discordwhitelist.command.lookup";

	public LookupCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "lookup");
	}

	@Override
	public void register() {
		Command.Builder<Audience> builder = super.builder().permission(PERMISSION);

		super.manager.command(builder
				.literal("minecraft")
				.required("player", DataParser.dataParser())
				.handler(this::handleMinecraft)
		);

		super.manager.command(builder
				.literal("discord")
				.required("player", ArgumentParser.firstOf(LongParser.longParser(0), StringParser.stringParser())) //TODO Maybe add DiscordProfileParser?
				.handler(this::handleDiscord)
		);
	}

	private void handleMinecraft(CommandContext<Audience> context) {
		Data data = context.get("player");
		context.sender().sendMessage(dataToComponent(data));
	}

	private void handleDiscord(CommandContext<Audience> context) {
		//TODO Implementation
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
