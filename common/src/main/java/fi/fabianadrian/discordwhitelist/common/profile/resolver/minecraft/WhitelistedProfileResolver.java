package fi.fabianadrian.discordwhitelist.common.profile.resolver.minecraft;

import fi.fabianadrian.discordwhitelist.common.data.DataManager;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class WhitelistedProfileResolver implements ProfileResolver {
	private final DataManager dataManager;

	public WhitelistedProfileResolver(DataManager dataManager) {
		this.dataManager = dataManager;
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		return this.dataManager.findByMinecraftIdentifier(identifier).thenApply(data -> {
			if (data == null) {
				return null;
			}
			return data.minecraftProfile();
		});
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		return this.dataManager.findByMinecraftUsername(username).thenApply(data -> {
			if (data == null) {
				return null;
			}
			return data.minecraftProfile();
		});
	}
}
