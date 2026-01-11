package fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver;

import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ChainedProfileResolver implements ProfileResolver {
	private final List<ProfileResolver> resolvers;
	private final Logger logger;

	public ChainedProfileResolver(Logger logger, List<ProfileResolver> resolvers) {
		this.logger = logger;
		this.resolvers = resolvers;
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		CompletableFuture<@Nullable MinecraftProfile> future = CompletableFuture.completedFuture(null);

		for (ProfileResolver resolver : resolvers) {
			future = future.thenCompose(result -> {
				if (result != null) {
					return CompletableFuture.completedFuture(result);
				}
				return resolver.resolve(identifier);
			}).exceptionally(e -> {
				this.logger.error("Couldn't resolve profile", e);
				return null;
			});
		}

		return future;
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		CompletableFuture<@Nullable MinecraftProfile> future = CompletableFuture.completedFuture(null);

		for (ProfileResolver resolver : resolvers) {
			future = future.thenCompose(result -> {
				if (result != null) {
					return CompletableFuture.completedFuture(result);
				}
				return resolver.resolve(username);
			}).exceptionally(e -> {
				this.logger.error("Couldn't resolve profile", e);
				return null;
			});
		}

		return future;
	}
}
