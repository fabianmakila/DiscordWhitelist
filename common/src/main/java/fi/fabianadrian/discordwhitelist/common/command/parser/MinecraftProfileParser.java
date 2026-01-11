package fi.fabianadrian.discordwhitelist.common.command.parser;

import fi.fabianadrian.discordwhitelist.common.command.ContextKeys;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;

import java.util.concurrent.CompletionException;
import java.util.regex.Pattern;


public final class MinecraftProfileParser<C> implements ArgumentParser<C, MinecraftProfile> {
	private static final Pattern PATTERN = Pattern.compile("^\\w{1,16}$");

	public static <C> @NonNull ParserDescriptor<C, MinecraftProfile> minecraftProfileParser() {
		return ParserDescriptor.of(new MinecraftProfileParser<>(), MinecraftProfile.class);
	}

	public static <C> CommandComponent.@NonNull Builder<?, MinecraftProfile> minecraftProfileComponent() {
		return CommandComponent.<C, MinecraftProfile>builder().parser(minecraftProfileParser());
	}

	@Override
	public @NonNull ArgumentParseResult<@NonNull MinecraftProfile> parse(@NonNull CommandContext<@NonNull C> context, @NonNull CommandInput commandInput) {
		final String username = commandInput.peekString();

		if (!PATTERN.matcher(username).matches()) {
			return ArgumentParseResult.failure(new InvalidProfileNameException(username, context));
		}

		try {
			MinecraftProfile profile = context.get(ContextKeys.PROFILE_RESOLVER).resolve(username).join();
			if (profile == null) {
				return ArgumentParseResult.failure(new PlayerProfileParseException(username, context));
			}
			commandInput.readString();
			return ArgumentParseResult.success(profile);

		} catch (CompletionException e) {
			return ArgumentParseResult.failure(new PlayerProfileParseException(username, context));
		}
	}

	public static final class InvalidProfileNameException extends ParserException {
		private InvalidProfileNameException(
				final @NonNull String input,
				final @NonNull CommandContext<?> context
		) {
			super(
					MinecraftProfileParser.class,
					context,
					Caption.of("argument.parse.failure.profile.invalid"),
					CaptionVariable.of("input", input)
			);
		}
	}

	public static final class PlayerProfileParseException extends ParserException {
		private PlayerProfileParseException(
				final @NonNull String input,
				final @NonNull CommandContext<?> context
		) {
			super(
					MinecraftProfileParser.class,
					context,
					Caption.of("argument.parse.failure.profile"),
					CaptionVariable.of("input", input)
			);
		}
	}
}
