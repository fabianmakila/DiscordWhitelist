package fi.fabianadrian.discordwhitelist.common.profile.resolver;

import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProfileResolver {
	CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier);

	CompletableFuture<@Nullable MinecraftProfile> resolve(String username);
}
