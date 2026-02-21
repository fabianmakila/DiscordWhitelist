package fi.fabianadrian.discordwhitelist.common.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.storage.MariaDBStorage;
import fi.fabianadrian.discordwhitelist.common.storage.SQLiteStorage;
import fi.fabianadrian.discordwhitelist.common.storage.Storage;
import org.flywaydb.core.api.FlywayException;

import java.security.Key;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class DataManager {
	private final Executor executor = Executors.newSingleThreadExecutor();
	private final DiscordWhitelist discordWhitelist;
	private Storage storage;
	private final Cache<UUID, Data> cache = Caffeine.newBuilder()
			.maximumSize(1000)
			.expireAfterWrite(Duration.ofSeconds(5))
			.build();

	public DataManager(DiscordWhitelist discordWhitelist) {
		this.discordWhitelist = discordWhitelist;
	}

	public void load() throws SQLException {
		if (this.storage != null) {
			this.storage.close();
		}
		try {
			switch (this.discordWhitelist.config().storage().type()) {
				case SQLITE -> this.storage = new SQLiteStorage(this.discordWhitelist);
				case MARIADB -> this.storage = new MariaDBStorage(this.discordWhitelist);
			}
			this.storage.migrate();
		} catch (SQLException | FlywayException e) {
			this.discordWhitelist.logger().error("Error loading storage", e);
			throw e;
		}
	}

	public CompletableFuture<Data> findByMinecraftIdentifier(UUID minecraftIdentifier) {
		return CompletableFuture.supplyAsync(() -> this.cache.get(minecraftIdentifier, uuid -> {
			try {
				return this.storage.selectByMinecraftIdentifier(uuid);
			} catch (SQLException e) {
				this.discordWhitelist.logger().error("Couldn't find data", e);
				throw new CompletionException(e);
			}
		}), this.executor);
	}

	public CompletableFuture<Collection<Data>> findByDiscordIdentifier(long discordIdentifier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.storage.selectByDiscordIdentifier(discordIdentifier);
			} catch (SQLException e) {
				this.discordWhitelist.logger().error("Couldn't find data", e);
				throw new CompletionException(e);
			}
		}, this.executor);
	}

	public CompletableFuture<Void> save(Data data) {
		return CompletableFuture.runAsync(() -> {
			try {
				this.storage.upsert(data);
			} catch (SQLException e) {
				this.discordWhitelist.logger().error("Couldn't save data", e);
				throw new CompletionException(e);
			}
		}, this.executor);
	}

	public CompletableFuture<Boolean> deleteByMinecraftIdentifier(UUID minecraftIdentifier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.storage.deleteByMinecraftIdentifier(minecraftIdentifier);
			} catch (SQLException e) {
				this.discordWhitelist.logger().error("Couldn't delete data", e);
				throw new CompletionException(e);
			}
		}, this.executor);
	}

	public CompletableFuture<Integer> deleteByDiscordIdentifier(long discordIdentifier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.storage.deleteByDiscordIdentifier(discordIdentifier);
			} catch (SQLException e) {
				this.discordWhitelist.logger().error("Couldn't delete data", e);
				throw new CompletionException(e);
			}
		}, this.executor);
	}
}
