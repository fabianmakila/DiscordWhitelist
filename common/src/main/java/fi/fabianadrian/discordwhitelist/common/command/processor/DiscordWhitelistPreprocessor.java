package fi.fabianadrian.discordwhitelist.common.command.processor;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.ContextKeys;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessor;

public final class DiscordWhitelistPreprocessor<S> implements CommandPreprocessor<S> {
	private final DiscordWhitelist discordWhitelist;

	public DiscordWhitelistPreprocessor(DiscordWhitelist discordWhitelist) {
		this.discordWhitelist = discordWhitelist;
	}

	@Override
	public void accept(@NonNull CommandPreprocessingContext<S> context) {
		CommandContext<S> commandContext = context.commandContext();
		commandContext.store(ContextKeys.DISCORD_WHITELIST, this.discordWhitelist);
	}
}
