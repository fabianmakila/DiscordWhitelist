package fi.fabianadrian.discordwhitelist.common.profile.resolver.minecraft;

import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class LuckPermsProfileResolver implements ProfileResolver {
	private LuckPerms luckPerms = null;

	public LuckPermsProfileResolver(Logger logger) {
		try {
			this.luckPerms = LuckPermsProvider.get();
			logger.info("Using LuckPerms to resolve profiles");
		} catch (NoClassDefFoundError ignored) {
		}
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		if (this.luckPerms == null) {
			return CompletableFuture.completedFuture(null);
		}

		return luckPerms.getUserManager().lookupUsername(identifier).thenApply(username -> {
			if (username == null) {
				return null;
			}
			return new MinecraftProfile(identifier, username);
		});
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		if (this.luckPerms == null) {
			return CompletableFuture.completedFuture(null);
		}

		return luckPerms.getUserManager().lookupUniqueId(username).thenApply(uuid -> {
			if (uuid == null) {
				return null;
			}
			return new MinecraftProfile(uuid, username);
		});
	}
}
