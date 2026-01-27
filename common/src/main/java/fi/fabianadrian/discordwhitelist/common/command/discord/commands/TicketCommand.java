package fi.fabianadrian.discordwhitelist.common.command.discord.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.discord.DiscordCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.discord.jda6.JDAInteraction;

public final class TicketCommand extends DiscordCommand {
	public TicketCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "ticket");
	}

	@Override
	public void register() {
		super.manager.command(super.builder.handler(this::handle));
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
