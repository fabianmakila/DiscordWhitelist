package fi.fabianadrian.discordwhitelist.common.command.minecraft.commands;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.command.minecraft.MinecraftCommand;
import fi.fabianadrian.discordwhitelist.common.command.parser.MinecraftProfileParser;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.CommandExecutionException;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.standard.LongParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.parser.standard.UUIDParser;
import org.incendo.cloud.type.Either;

import java.util.UUID;

public final class LookupCommand extends MinecraftCommand {
	private static final String PERMISSION = "discordwhitelist.command.lookup";
	private static final TranslatableComponent COMPONENT_MINECRAFT_EMPTY = Component.translatable("discordwhitelist.command.minecraft.lookup.minecraft.empty");

	public LookupCommand(DiscordWhitelist discordWhitelist) {
		super(discordWhitelist, "lookup");
	}

	@Override
	public void register() {
		Command.Builder<Audience> builder = super.builder().permission(PERMISSION);

		super.manager.command(builder
				.literal("minecraft")
				.required("identifier|username", ArgumentParser.firstOf(UUIDParser.uuidParser(), MinecraftProfileParser.minecraftProfileParser()))
				.handler(this::handleMinecraft)
		);

		super.manager.command(builder
				.literal("discord")
				.required("identifier|username", ArgumentParser.firstOf(LongParser.longParser(0), StringParser.stringParser())) //TODO Maybe add DiscordProfileParser?
				.handler(this::handleDiscord)
		);
	}

	private void handleMinecraft(CommandContext<Audience> context) {
		Either<UUID, MinecraftProfile> either = context.get("identifier|username");
		UUID uuid;
		if (either.primary().isPresent()) {
			uuid = either.primary().get();
		} else {
			uuid = either.fallback().get().identifier();
		}
		super.discordWhitelist.dataManager().findByMinecraftIdentifier(uuid).thenAccept(data -> {
			if (data == null) {
				context.sender().sendMessage(COMPONENT_MINECRAFT_EMPTY);
				return;
			}
			context.sender().sendMessage(dataToComponent(data));
		}).exceptionally(throwable -> {
			throw new CommandExecutionException(throwable);
		});
	}

	private void handleDiscord(CommandContext<Audience> context) {
		//TODO Implementation
	}

	private Component dataToComponent(Data data) {
		return Component.join(
				JoinConfiguration.newlines(),
				Component.translatable(
						"discordwhitelist.command.minecraft.lookup.discord.identifier",
						Argument.string("identifier", String.valueOf(data.discordProfile().identifier()))
				),
				Component.translatable(
						"discordwhitelist.command.minecraft.lookup.discord.username",
						Argument.string("username", data.discordProfile().username())
				),
				Component.translatable(
						"discordwhitelist.command.minecraft.lookup.minecraft.identifier",
						Argument.string("identifier", String.valueOf(data.minecraftProfile().identifier()))
				),
				Component.translatable(
						"discordwhitelist.command.minecraft.lookup.minecraft.username",
						Argument.string("username", data.minecraftProfile().username())
				)
		);
	}
}
