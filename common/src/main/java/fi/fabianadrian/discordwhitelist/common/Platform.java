package fi.fabianadrian.discordwhitelist.common;

import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ChainedProfileResolver;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.incendo.cloud.CommandManager;
import org.slf4j.Logger;

import java.nio.file.Path;

public interface Platform extends ForwardingAudience.Single {
	Path dataDirectory();

	Logger logger();

	ChainedProfileResolver profileResolver();

	CommandManager<Audience> commandManager();
}
