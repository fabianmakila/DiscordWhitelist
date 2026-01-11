package fi.fabianadrian.discordwhitelist.paper.profile;

import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ProfileResolver;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class OnlineProfileResolver implements ProfileResolver {
	private final Server server;

	public OnlineProfileResolver(Server server) {
		this.server = server;
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		Player player = this.server.getPlayer(identifier);

		if (player == null) {
			return CompletableFuture.completedFuture(null);
		}

		return CompletableFuture.completedFuture(new MinecraftProfile(player.getUniqueId(), player.getName()));
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		Player player = this.server.getPlayer(username);

		if (player == null) {
			return CompletableFuture.completedFuture(null);
		}

		return CompletableFuture.completedFuture(new MinecraftProfile(player.getUniqueId(), player.getName()));
	}
}
