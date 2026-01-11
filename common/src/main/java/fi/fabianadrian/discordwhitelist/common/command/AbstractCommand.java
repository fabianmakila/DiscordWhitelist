package fi.fabianadrian.discordwhitelist.common.command;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;

public abstract class AbstractCommand {
	protected final DiscordWhitelist discordWhitelist;

	public AbstractCommand(DiscordWhitelist discordWhitelist) {
		this.discordWhitelist = discordWhitelist;
	}

	public abstract void register();
}
