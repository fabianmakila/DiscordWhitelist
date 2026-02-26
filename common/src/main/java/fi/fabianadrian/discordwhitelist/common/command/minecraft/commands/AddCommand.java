package fi.fabianadrian.discordwhitelist.common.command.minecraft.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.minecraft.MinecraftCommand;
import fi.fabianadrian.discordwhitelist.common.command.parser.MinecraftProfileParser;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.context.CommandContext;

public final class AddCommand extends MinecraftCommand {
	private static final String PERMISSION = "discordwhitelist.command.add";

	public AddCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "add");
	}

	@Override
	public void register() {
		var builder = super.builder()
				.permission(PERMISSION)
				.required("player", MinecraftProfileParser.minecraftProfileParser())
				.handler(this::handle);
		super.manager.command(builder);
	}

	private void handle(CommandContext<Audience> context) {

	}
}
