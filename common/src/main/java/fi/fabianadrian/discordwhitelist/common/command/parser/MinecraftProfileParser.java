package fi.fabianadrian.discordwhitelist.common.command.parser;

import fi.fabianadrian.discordwhitelist.common.command.ContextKeys;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ParserDescriptor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public final class MinecraftProfileParser<C> extends MinecraftParser<C, MinecraftProfile> {
	public static <C> @NonNull ParserDescriptor<C, MinecraftProfile> minecraftProfileParser() {
		return ParserDescriptor.of(new MinecraftProfileParser<>(), MinecraftProfile.class);
	}

	@Override
	protected CompletableFuture<ArgumentParseResult<MinecraftProfile>> parseByUUID(CommandContext<C> context, CommandInput input, UUID uuid) {
		final ProfileResolver resolver = context.get(ContextKeys.PROFILE_RESOLVER);
		return resolver.resolve(uuid).thenApply(profile -> {
			if (profile == null) {
				return ArgumentParseResult.failure(new PlayerProfileParseException(uuid.toString(), context));
			}
			input.readString();
			return ArgumentParseResult.success(profile);
		});
	}

	@Override
	protected CompletableFuture<ArgumentParseResult<MinecraftProfile>> parseByUsername(CommandContext<C> context, CommandInput input, String username) {
		final ProfileResolver resolver = context.get(ContextKeys.PROFILE_RESOLVER);
		return resolver.resolve(username).thenApply(profile -> {
			if (profile == null) {
				return ArgumentParseResult.failure(new PlayerProfileParseException(username, context));
			}
			input.readString();
			return ArgumentParseResult.success(profile);
		});
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
