package fi.fabianadrian.discordwhitelist.common.storage;

import fi.fabianadrian.discordwhitelist.common.data.Data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public interface Storage {
	void createTable() throws SQLException;
	Collection<Data> selectByDiscordIdentifier(long discordIdentifier) throws SQLException;
	Data selectByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException;

	void upsert(Data data) throws SQLException;
	boolean deleteByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException;
	int deleteByDiscordIdentifier(long discordIdentifier) throws SQLException;
	default void close() {

	}
}
