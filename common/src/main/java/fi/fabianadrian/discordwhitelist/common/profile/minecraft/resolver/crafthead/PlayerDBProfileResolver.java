package fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.crafthead;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.resolver.ProfileResolver;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class PlayerDBProfileResolver implements ProfileResolver {
	private HttpClient httpClient;

	public PlayerDBProfileResolver() {
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[]{new SSLBypassTrustManager()}, new SecureRandom());
			this.httpClient = HttpClient.newBuilder()
					//.sslContext(context)
					.build();
		} catch (Exception e) {
			this.httpClient = null;
		}
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		return fetch(identifier.toString());
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		return fetch(username);
	}

	private CompletableFuture<MinecraftProfile> fetch(String input) {
		if (this.httpClient == null) {
			return CompletableFuture.completedFuture(null);
		}

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://playerdb.co/api/player/minecraft/" + input.toLowerCase(Locale.ROOT)))
				.header("User-Agent", "DiscordWhitelist/1.0")
				.GET()
				.build();

		return this.httpClient
				.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(response -> {
					if (response.statusCode() == 400) {
						return null;
					}
					if (response.statusCode() != 200) {
						throw new CompletionException(new RuntimeException("Response: " + response.statusCode()));
					}

					JsonObject playerObject = JsonParser.parseString(response.body()).getAsJsonObject().get("data")
							.getAsJsonObject()
							.get("player")
							.getAsJsonObject();


					UUID uuid = UUID.fromString(playerObject.get("id").getAsString());
					String name = playerObject.get("username").getAsString();

					return new MinecraftProfile(uuid, name);
				});
	}
}
