package fi.fabianadrian.discordwhitelist.velocity.profile;

import com.velocitypowered.api.proxy.ProxyServer;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class OnlineProfileResolver implements ProfileResolver {
	private final ProxyServer server;

	public OnlineProfileResolver(ProxyServer server) {
		this.server = server;
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		return CompletableFuture.completedFuture(
				this.server.getPlayer(identifier)
						.map(player -> new MinecraftProfile(player.getUniqueId(), player.getUsername()))
						.orElse(null)
		);
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		return CompletableFuture.completedFuture(
				this.server.getPlayer(username)
						.map(player -> new MinecraftProfile(player.getUniqueId(), player.getUsername()))
						.orElse(null)
		);
	}
}
