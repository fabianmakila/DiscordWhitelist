package fi.fabianadrian.discordwhitelist.common.command.minecraft.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.minecraft.MinecraftCommand;
import fi.fabianadrian.discordwhitelist.common.command.parser.DataParser;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.data.DataManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.CommandExecutionException;

public final class RemoveCommand extends MinecraftCommand {
	private static final String PERMISSION = "discordwhitelist.command.remove";
	private final DataManager dataManager;

	public RemoveCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "remove");
		this.dataManager = discordWhitelist.dataManager();
	}

	@Override
	public void register() {
		var builder = super.builder()
				.permission(PERMISSION)
				.required("player", DataParser.dataParser())
				.handler(this::handle);
		super.manager.command(builder);
	}

	private void handle(CommandContext<Audience> context) {
		Data data = context.get("player");
		this.dataManager.deleteByMinecraftIdentifier(data)
				.thenAccept(success -> context.sender().sendMessage(Component.translatable(
						"discordwhitelist.command.minecraft.remove",
						Argument.component("player", data.minecraftProfile().asComponent())
				))).exceptionally(throwable -> {
					throw new CommandExecutionException(throwable);
				});
	}
}
