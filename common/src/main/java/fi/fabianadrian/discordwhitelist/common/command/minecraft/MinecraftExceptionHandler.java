package fi.fabianadrian.discordwhitelist.common.command.minecraft;

import fi.fabianadrian.discordwhitelist.common.command.AbstractExceptionHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TranslatableComponent;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.exception.handling.ExceptionContext;
import org.slf4j.Logger;

public final class MinecraftExceptionHandler extends AbstractExceptionHandler<Audience> {
	private final MinecraftCaptionFormatter formatter = new MinecraftCaptionFormatter();

	public MinecraftExceptionHandler(Logger logger) {
		super(logger);
	}

	@Override
	protected void send(ExceptionContext<Audience, ?> context, Caption caption, CaptionVariable... variables) {
		TranslatableComponent component = context.context().formatCaption(this.formatter, caption, variables);
		context.context().sender().sendMessage(component);
	}
}
