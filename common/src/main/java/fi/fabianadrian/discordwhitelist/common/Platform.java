package fi.fabianadrian.discordwhitelist.common;

import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ChainedProfileResolver;
import org.slf4j.Logger;

import java.nio.file.Path;

public interface Platform {
	Path dataDirectory();

	Logger logger();

	ChainedProfileResolver profileResolver();
}
