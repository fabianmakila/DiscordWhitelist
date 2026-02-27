package fi.fabianadrian.discordwhitelist.common.command.discord;

import fi.fabianadrian.discordwhitelist.common.command.AbstractExceptionHandler;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.discord.jda6.JDAInteraction;
import org.incendo.cloud.exception.handling.ExceptionContext;
import org.slf4j.Logger;

public final class DiscordExceptionHandler extends AbstractExceptionHandler<JDAInteraction> {
	public DiscordExceptionHandler(Logger logger) {
		super(logger);
	}

	@Override
	protected void send(ExceptionContext<JDAInteraction, ?> context, Caption caption, CaptionVariable... variables) {
		String message = context.context().formatCaption(caption, variables);
		context.context().sender().interactionEvent().getHook().sendMessage(message);
	}
}
