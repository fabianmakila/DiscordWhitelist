package fi.fabianadrian.discordwhitelist.common.command.minecraft.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.minecraft.MinecraftCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.CommandExecutionException;

import java.sql.SQLException;

public final class ReloadCommand extends MinecraftCommand {
	private static final String PERMISSION = "discordwhitelist.command.reload";
	private static final Component COMPONENT = Component.translatable("discordwhitelist.command.minecraft.reload");

	public ReloadCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "reload");
	}

	@Override
	public void register() {
		Command.Builder<Audience> builder = super.builder().permission(PERMISSION).handler(this::handle);
		super.manager.command(builder);
	}

	private void handle(CommandContext<Audience> context) {
		try {
			super.discordWhitelist.load();
			context.sender().sendMessage(COMPONENT);
		} catch (SQLException exception) {
			throw new CommandExecutionException(exception);
		}
	}
}
