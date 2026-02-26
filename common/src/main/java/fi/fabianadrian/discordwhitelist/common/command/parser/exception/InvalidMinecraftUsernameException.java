package fi.fabianadrian.discordwhitelist.common.command.parser.exception;

import fi.fabianadrian.discordwhitelist.common.command.parser.MinecraftProfileParser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;

public class InvalidMinecraftUsernameException extends ParserException {
	public InvalidMinecraftUsernameException(
			final @NonNull String input,
			final @NonNull CommandContext<?> context
	) {
		super(
				MinecraftProfileParser.class,
				context,
				Caption.of("argument.parse.failure.minecraft.invalid"),
				CaptionVariable.of("input", input)
		);
	}
}
