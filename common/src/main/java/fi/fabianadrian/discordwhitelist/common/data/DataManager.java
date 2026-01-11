package fi.fabianadrian.discordwhitelist.common.data;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.storage.Storage;
import fi.fabianadrian.discordwhitelist.common.storage.sqlite.SQLiteStorage;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class DataManager {
	private final Storage storage;
	private final Executor executor = Executors.newSingleThreadExecutor();

	public DataManager(DiscordWhitelist discordWhitelist) {
		this.storage = new SQLiteStorage(discordWhitelist);
	}

	public CompletableFuture<Void> init() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.storage.createTable();
			} catch (SQLException e) {
				throw new CompletionException(e);
			}
		}, this.executor);
	}

	public CompletableFuture<Data> findByMinecraftIdentifier(UUID minecraftIdentifier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.storage.selectByMinecraftIdentifier(minecraftIdentifier);
			} catch (SQLException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<Collection<Data>> findByDiscordIdentifier(long discordIdentifier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.storage.selectByDiscordIdentifier(discordIdentifier);
			} catch (SQLException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<Void> save(Data data) {
		return CompletableFuture.runAsync(() -> {
			try {
				this.storage.upsert(data);
			} catch (SQLException e) {
				throw new CompletionException(e);
			}
		});
	}

	CompletableFuture<Boolean> deleteByMinecraftIdentifier(UUID minecraftIdentifier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.storage.deleteByMinecraftIdentifier(minecraftIdentifier);
			} catch (SQLException e) {
				throw new CompletionException(e);
			}
		});
	}

	CompletableFuture<Integer> deleteByDiscordIdentifier(long discordIdentifier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.storage.deleteByDiscordIdentifier(discordIdentifier);
			} catch (SQLException e) {
				throw new CompletionException(e);
			}
		});
	}
}
