package fi.fabianadrian.discordwhitelist.common.command.parser;

import fi.fabianadrian.discordwhitelist.common.command.parser.exception.InvalidMinecraftUsernameException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class MinecraftParser<C, T> implements ArgumentParser.FutureArgumentParser<C, T> {
	private static final Pattern PATTERN = Pattern.compile("^\\w{1,16}$");

	@Override
	public @NonNull CompletableFuture<@NonNull ArgumentParseResult<T>> parseFuture(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
		final String inputString = input.peekString();

		CompletableFuture<ArgumentParseResult<T>> future;
		try {
			UUID uuid = UUID.fromString(inputString);
			future = parseByUUID(context, input, uuid);
		} catch (IllegalArgumentException exception) {
			if (!PATTERN.matcher(inputString).matches()) {
				return CompletableFuture.completedFuture(ArgumentParseResult.failure(new InvalidMinecraftUsernameException(inputString, context)));
			}
			future = parseByUsername(context, input, inputString);
		}
		return future;
	}

	protected abstract CompletableFuture<ArgumentParseResult<T>> parseByUUID(
			CommandContext<C> context,
			CommandInput input,
			UUID uuid
	);

	protected abstract CompletableFuture<ArgumentParseResult<T>> parseByUsername(
			CommandContext<C> context,
			CommandInput input,
			String username
	);
}
