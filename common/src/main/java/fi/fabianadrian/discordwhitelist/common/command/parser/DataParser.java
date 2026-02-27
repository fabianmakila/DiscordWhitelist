package fi.fabianadrian.discordwhitelist.common.command.parser;

import fi.fabianadrian.discordwhitelist.common.command.ContextKeys;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.data.DataManager;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.concurrent.CompletableFuture;

public final class DataParser<C> implements ArgumentParser.FutureArgumentParser<C, Data>, SuggestionProvider<String> {
	public static <C> @NonNull ParserDescriptor<C, MinecraftProfile> dataParser() {
		return ParserDescriptor.of(new MinecraftProfileParser<>(), MinecraftProfile.class);
	}

	@Override
	public @NonNull CompletableFuture<@NonNull ArgumentParseResult<Data>> parseFuture(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
		DataManager dataManager = context.get(ContextKeys.DISCORD_WHITELIST).dataManager();
		return MinecraftProfileParser.<C>minecraftProfileParser().parser().parseFuture(context, input).thenCompose(result -> {
			if (result.failure().isPresent()) {
				return CompletableFuture.completedFuture(ArgumentParseResult.failure(result.failure().get()));
			}

			if (result.parsedValue().isEmpty()) {
				return CompletableFuture.completedFuture(ArgumentParseResult.failure(new IllegalStateException("parsed value was null")));
			}

			MinecraftProfile profile = result.parsedValue().get();
			return dataManager.findByMinecraftIdentifier(profile.identifier())
					.thenApply(data -> {
						if (data == null) {
							return ArgumentParseResult.failure(new DataParseException(profile.username(), context));
						}
						return ArgumentParseResult.success(data);
					});
		});
	}

	@Override
	public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext context, @NonNull CommandInput input) {
		return context.get(ContextKeys.DISCORD_WHITELIST).onlinePlayerNames().thenApply(list -> list.map(Suggestion::suggestion).toList());
	}

	public static final class DataParseException extends ParserException {
		private DataParseException(final @NonNull String input, final @NonNull CommandContext<?> context) {
			super(
					MinecraftProfileParser.class,
					context,
					Caption.of("argument.parse.failure.data"),
					CaptionVariable.of("input", input)
			);
		}
	}
}
