package fi.fabianadrian.discordwhitelist.common.command.discord.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.discord.DiscordCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda6.JDAInteraction;

public final class TicketCommand extends DiscordCommand {
	public TicketCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist);
	}

	@Override
	public void register() {
		Command.Builder<JDAInteraction> builder = super.manager.commandBuilder("ticket").handler(this::handle);
		super.manager.command(builder);
	}

	private void handle(CommandContext<JDAInteraction> context) {
		GenericCommandInteractionEvent event = context.sender().interactionEvent();
		if (event == null) {
			super.discordWhitelist.logger().warn("GenericCommandInteractionEvent was null");
			return;
		}

		User user = event.getUser();
		this.discordWhitelist.discordBot().createTicketChannel(user)
				.thenRun(() -> sendMessage(context, "discord.ticket.ticket-created"))
				.exceptionally(throwable -> {
					super.discordWhitelist.logger().warn("Failed to create ticket channel", throwable);
					sendMessage(context, "discord.ticket.creation-failed");
					return null;
				})
		;
	}
}
