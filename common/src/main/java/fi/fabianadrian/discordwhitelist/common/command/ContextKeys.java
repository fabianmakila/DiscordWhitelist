package fi.fabianadrian.discordwhitelist.common.command;

import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ProfileResolver;
import org.incendo.cloud.key.CloudKey;

public final class ContextKeys {
	public static final CloudKey<ProfileResolver> PROFILE_RESOLVER = CloudKey.of("ProfileResolver", ProfileResolver.class);

	private ContextKeys() {
	}
}
