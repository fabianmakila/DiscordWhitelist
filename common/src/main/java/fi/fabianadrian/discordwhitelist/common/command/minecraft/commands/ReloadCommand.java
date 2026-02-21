package fi.fabianadrian.discordwhitelist.common.command.minecraft.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.minecraft.MinecraftCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;

import java.sql.SQLException;

public final class ReloadCommand extends MinecraftCommand {
	private static final String PERMISSION = "discordwhitelist.command.reload";
	private static final Component COMPONENT_SUCCESS = Component.translatable("discordwhitelist.command.reload.success");
	private static final Component COMPONENT_FAILURE = Component.translatable("discordwhitelist.command.reload.failure");

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
			context.sender().sendMessage(COMPONENT_SUCCESS);
		} catch (SQLException e) {
			context.sender().sendMessage(COMPONENT_FAILURE);
		}
	}
}
