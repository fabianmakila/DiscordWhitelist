package fi.fabianadrian.discordwhitelist.common.command.minecraft;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.AbstractCommand;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public abstract class MinecraftCommand extends AbstractCommand {
	protected final CommandManager<Audience> manager;
	private final Command.Builder<Audience> builder;

	public MinecraftCommand(DiscordWhitelist discordWhitelist, String name, String... aliases) {
		super(discordWhitelist);
		this.manager = discordWhitelist.minecraftCommandManager();
		this.builder = this.manager.commandBuilder("discordwhitelist").literal(name, aliases);
	}

	public Command.Builder<Audience> builder() {
		return this.builder;
	}
}
