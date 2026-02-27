package fi.fabianadrian.discordwhitelist.common.command.minecraft.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.minecraft.MinecraftCommand;
import fi.fabianadrian.discordwhitelist.common.command.parser.MinecraftProfileParser;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.data.DataManager;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.CommandExecutionException;

public final class AddCommand extends MinecraftCommand {
	private static final String PERMISSION = "discordwhitelist.command.add";
	private final DataManager dataManager;

	public AddCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "add");
		this.dataManager = discordWhitelist.dataManager();
	}

	@Override
	public void register() {
		var builder = super.builder()
				.permission(PERMISSION)
				.required("player", MinecraftProfileParser.minecraftProfileParser())
				.handler(this::handle);
		super.manager.command(builder);
	}

	private void handle(CommandContext<Audience> context) {
		MinecraftProfile profile = context.get("player");
		this.dataManager.findByMinecraftIdentifier(profile.identifier()).thenCompose(data -> {
			Component component;
			if (data != null) {
				component = Component.translatable("discordwhitelist.command.minecraft.add.already-added", profile.asComponent());
				data.minecraftProfile().username(profile.username());
			} else {
				component = Component.translatable("discordwhitelist.command.minecraft.add", profile.asComponent());
				data = new Data(profile);
			}

			return this.dataManager.save(data)
					.exceptionally(throwable -> {
						throw new CommandExecutionException(throwable);
					})
					.thenRun(() -> context.sender().sendMessage(component));

		});
	}
}
