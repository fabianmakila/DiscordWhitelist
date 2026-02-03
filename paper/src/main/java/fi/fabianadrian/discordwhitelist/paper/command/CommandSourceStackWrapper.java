package fi.fabianadrian.discordwhitelist.paper.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.NotNull;

public final class CommandSourceStackWrapper implements ForwardingAudience.Single {
	private final CommandSourceStack stack;

	public CommandSourceStackWrapper(CommandSourceStack stack) {
		this.stack = stack;
	}

	@Override
	public @NotNull Audience audience() {
		return this.stack.getSender();
	}

	public CommandSourceStack stack() {
		return this.stack;
	}
}
