package fi.fabianadrian.discordwhitelist.common.profile.resolver.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolveException;
import fi.fabianadrian.discordwhitelist.common.profile.resolver.ProfileResolver;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerDBProfileResolver implements ProfileResolver {
	private static final URI BASE_URI = URI.create("https://playerdb.co/api/player/minecraft/");
	private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
			.header("User-Agent", "DiscordWhitelist/1.0");
	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(UUID identifier) {
		return fetch(identifier.toString());
	}

	@Override
	public CompletableFuture<@Nullable MinecraftProfile> resolve(String username) {
		return fetch(username);
	}

	private CompletableFuture<MinecraftProfile> fetch(String input) {
		HttpRequest request = this.requestBuilder.uri(BASE_URI.resolve(input)).build();
		return this.httpClient
				.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.thenApply(JsonParser::parseString)
				.thenApply(JsonElement::getAsJsonObject)
				.thenApply(object -> {
					String code = object.get("code").getAsString();
					return switch (code) {
						case "player.found" -> {
							JsonObject playerObject = object
									.getAsJsonObject("data")
									.getAsJsonObject("player");

							UUID uuid = UUID.fromString(playerObject.get("id").getAsString());
							String name = playerObject.get("username").getAsString();
							yield new MinecraftProfile(uuid, name);
						}
						case "minecraft.invalid_username" -> null;
						default -> {
							String message = object.get("message").getAsString();
							throw new ProfileResolveException(message);
						}
					};
				});
	}
}
