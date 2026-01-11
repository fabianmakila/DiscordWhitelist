package fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver;

import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class LuckPermsProfileResolver implements ProfileResolver {
	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		LuckPerms luckPerms = LuckPermsProvider.get();
		return luckPerms.getUserManager().lookupUsername(identifier).thenApply(username -> {
			if (username == null) {
				return null;
			}
			return new MinecraftProfile(identifier, username);
		});
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		LuckPerms luckPerms = LuckPermsProvider.get();
		return luckPerms.getUserManager().lookupUniqueId(username).thenApply(uuid -> {
			if (uuid == null) {
				return null;
			}
			return new MinecraftProfile(uuid, username);
		});
	}
}
