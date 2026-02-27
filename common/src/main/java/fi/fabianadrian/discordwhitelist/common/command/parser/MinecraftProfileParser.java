package fi.fabianadrian.discordwhitelist.common.command.parser;

import fi.fabianadrian.discordwhitelist.common.command.ContextKeys;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;


public final class MinecraftProfileParser<C> implements ArgumentParser.FutureArgumentParser<C, MinecraftProfile> {
	private static final Pattern PATTERN = Pattern.compile("^\\w{1,16}$");

	public static <C> @NonNull ParserDescriptor<C, MinecraftProfile> minecraftProfileParser() {
		return ParserDescriptor.of(new MinecraftProfileParser<>(), MinecraftProfile.class);
	}

	public static <C> CommandComponent.@NonNull Builder<C, MinecraftProfile> playerProfileComponent() {
		return CommandComponent.<C, MinecraftProfile>builder().parser(minecraftProfileParser());
	}

	@Override
	public @NonNull CompletableFuture<@NonNull ArgumentParseResult<MinecraftProfile>> parseFuture(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
		final String inputString = input.peekString();
		final ProfileResolver resolver = context.get(ContextKeys.DISCORD_WHITELIST).profileResolver();

		CompletableFuture<MinecraftProfile> future;
		try {
			UUID uuid = UUID.fromString(inputString);
			future = resolver.resolve(uuid);
		} catch (IllegalArgumentException exception) {
			if (!PATTERN.matcher(inputString).matches()) {
				return CompletableFuture.completedFuture(ArgumentParseResult.failure(new PlayerProfileInvalidUsernameException(inputString, context)));
			}
			future = resolver.resolve(inputString);
		}

		return future.thenApply(profile -> {
			if (profile == null) {
				return ArgumentParseResult.failure(new PlayerProfileParseException(inputString, context));
			}
			input.readString();
			return ArgumentParseResult.success(profile);
		});
	}

	public static final class PlayerProfileInvalidUsernameException extends ParserException {
		private PlayerProfileInvalidUsernameException(
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
		private PlayerProfileParseException(final @NonNull String input, final @NonNull CommandContext<?> context) {
			super(
					MinecraftProfileParser.class,
					context,
					Caption.of("argument.parse.failure.profile"),
					CaptionVariable.of("input", input)
			);
		}
	}
}
