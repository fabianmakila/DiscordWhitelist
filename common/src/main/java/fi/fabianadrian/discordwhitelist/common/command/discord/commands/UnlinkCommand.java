package fi.fabianadrian.discordwhitelist.common.command.discord.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.discord.DiscordCommand;
import fi.fabianadrian.discordwhitelist.common.data.DataManager;
import net.dv8tion.jda.api.entities.User;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda6.JDAInteraction;
import org.incendo.cloud.discord.jda6.JDAParser;

public final class UnlinkCommand extends DiscordCommand {
	private final DataManager dataManager;

	public UnlinkCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "unlink");
		this.dataManager = discordWhitelist.dataManager();
	}

	@Override
	public void register() {
		var builder = super.builder
				.required("discord-username", JDAParser.userParser())
				.handler(this::handle);

		super.manager.command(builder);
	}

	private void handle(CommandContext<JDAInteraction> context) {
		User user = context.get("discord-username");
		int modified = this.dataManager.deleteByDiscordIdentifier(user.getIdLong()).join();
		if (modified > 0) {
			sendMessage(context, "discord.unlink");
		} else {
			sendMessage(context, "discord.unlink.not-linked");
		}
	}
}
