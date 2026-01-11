package fi.fabianadrian.discordwhitelist.common.command.discord;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda6.JDA6CommandManager;
import org.incendo.cloud.discord.jda6.JDAInteraction;

public abstract class DiscordCommand extends AbstractCommand {
	protected final JDA6CommandManager<JDAInteraction> manager;

	public DiscordCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist);
		this.manager = discordWhitelist.discordCommandManager();
	}

	protected void sendMessage(CommandContext<JDAInteraction> context, String key, CaptionVariable... variables) {
		GenericCommandInteractionEvent event = context.sender().interactionEvent();
		event.getHook().sendMessage(context.formatCaption(Caption.of(key), variables)).queue();
	}
}
