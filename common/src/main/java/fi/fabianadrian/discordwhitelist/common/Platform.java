package fi.fabianadrian.discordwhitelist.common;

import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.incendo.cloud.CommandManager;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface Platform extends ForwardingAudience.Single {
	Path dataDirectory();

	Logger logger();

	ProfileResolver onlineProfileResolver();

	CommandManager<Audience> commandManager();

	CompletableFuture<Stream<String>> onlinePlayerNames();
}
