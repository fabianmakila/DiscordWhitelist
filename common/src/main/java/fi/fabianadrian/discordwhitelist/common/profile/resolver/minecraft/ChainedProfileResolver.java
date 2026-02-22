package fi.fabianadrian.discordwhitelist.common.profile.resolver.minecraft;

import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ChainedProfileResolver implements ProfileResolver {
	private final List<ProfileResolver> resolvers;

	public ChainedProfileResolver(List<ProfileResolver> resolvers) {
		this.resolvers = resolvers;
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		CompletableFuture<MinecraftProfile> future = CompletableFuture.completedFuture(null);

		for (ProfileResolver resolver : resolvers) {
			future = future.thenCompose(result -> {
				if (result != null) {
					return CompletableFuture.completedFuture(result);
				}
				return resolver.resolve(identifier);
			});
		}

		return future;
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		CompletableFuture<MinecraftProfile> future = CompletableFuture.completedFuture(null);

		for (ProfileResolver resolver : resolvers) {
			future = future.thenCompose(result -> {
				if (result != null) {
					return CompletableFuture.completedFuture(result);
				}
				return resolver.resolve(username);
			});
		}

		return future;
	}
}
