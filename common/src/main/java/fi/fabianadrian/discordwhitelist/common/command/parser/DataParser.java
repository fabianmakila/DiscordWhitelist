package fi.fabianadrian.discordwhitelist.common.command.parser;

import fi.fabianadrian.discordwhitelist.common.command.ContextKeys;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.data.DataManager;
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

public final class DataParser<C> extends MinecraftParser<C, Data> {
	public static <C> @NonNull ParserDescriptor<C, Data> dataParser() {
		return ParserDescriptor.of(new DataParser<>(), Data.class);
	}

	@Override
	protected CompletableFuture<ArgumentParseResult<Data>> parseByUUID(CommandContext<C> context, CommandInput input, UUID identifier) {
		final DataManager dataManager = context.get(ContextKeys.DATA_MANAGER);
		return dataManager.findByMinecraftIdentifier(identifier).thenApply(profile -> {
			if (profile == null) {
				return ArgumentParseResult.failure(new DataParseException(identifier.toString(), context));
			}
			input.readString();
			return ArgumentParseResult.success(profile);
		});
	}

	@Override
	protected CompletableFuture<ArgumentParseResult<Data>> parseByUsername(CommandContext<C> context, CommandInput input, String username) {
		final DataManager dataManager = context.get(ContextKeys.DATA_MANAGER);
		return dataManager.findByMinecraftUsername(username).thenApply(profile -> {
			if (profile == null) {
				return ArgumentParseResult.failure(new DataParseException(username, context));
			}
			input.readString();
			return ArgumentParseResult.success(profile);
		});
	}

	public static final class DataParseException extends ParserException {
		private DataParseException(
				final @NonNull String input,
				final @NonNull CommandContext<?> context
		) {
			super(
					MinecraftProfileParser.class,
					context,
					Caption.of("argument.parse.failure.data"),
					CaptionVariable.of("input", input)
			);
		}
	}
}
