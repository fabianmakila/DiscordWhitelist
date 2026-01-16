package fi.fabianadrian.discordwhitelist.common.storage.sqlite;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.profile.DiscordProfile;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;
import fi.fabianadrian.discordwhitelist.common.storage.Storage;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class SQLiteStorage implements Storage {
	private static final String STATEMENT_CREATE_TABLE = """
			CREATE TABLE IF NOT EXISTS dw_data (
			minecraft_identifier BLOB PRIMARY KEY,
			minecraft_username  TEXT,
			discord_identifier TEXT,
			discord_username TEXT
			);
			""";
	private static final String STATEMENT_UPSERT = """
			INSERT INTO dw_data (
				minecraft_identifier,
				minecraft_username,
				discord_identifier,
				discord_username
			)
			VALUES (?, ?, ?, ?)
			ON CONFLICT(minecraft_identifier) DO UPDATE SET
				minecraft_username = excluded.minecraft_username,
				discord_identifier = excluded.discord_identifier,
				discord_username = excluded.discord_username
			""";
	private static final String STATEMENT_SELECT_BY_MINECRAFT_IDENTIFIER = """
			SELECT minecraft_username,
				discord_identifier,
				discord_username
			FROM dw_data
			WHERE minecraft_identifier = ?
			""";
	private static final String STATEMENT_SELECT_BY_DISCORD_IDENTIFIER = """
			SELECT minecraft_identifier,
				minecraft_username,
				discord_username
			FROM dw_data
			WHERE discord_identifier = ?
			""";
	private final SQLiteConnectionFactory factory;

	public SQLiteStorage(DiscordWhitelist discordWhitelist) {
		this.factory = new SQLiteConnectionFactory(discordWhitelist.dataDirectory());
	}

	@Override
	public void createTable() throws SQLException {
		try (Connection connection = this.factory.connection()) {
			Statement statement = connection.createStatement();
			statement.execute(STATEMENT_CREATE_TABLE);
		}
	}

	@Override
	public Collection<Data> selectByDiscordIdentifier(long discordIdentifier) throws SQLException {
		try (Connection connection = this.factory.connection()) {
			PreparedStatement statement = connection.prepareStatement(STATEMENT_SELECT_BY_DISCORD_IDENTIFIER);
			statement.setString(1, String.valueOf(discordIdentifier));
			ResultSet resultSet = statement.executeQuery();

			List<Data> dataList = new ArrayList<>();
			while (resultSet.next()) {
				UUID minecraftIdentifier = bytesToUUID(resultSet.getBytes("minecraft_identifier"));

				String minecraftUsername = resultSet.getString("minecraft_username");
				MinecraftProfile minecraftProfile = new MinecraftProfile(minecraftIdentifier, minecraftUsername);
				Data data = new Data(minecraftProfile);

				String discordUsername = resultSet.getString("discord_username");
				data.discordProfile(new DiscordProfile(discordIdentifier, discordUsername));
				dataList.add(data);
			}

			return dataList;
		}
	}

	@Override
	public Data selectByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException {
		try (Connection connection = this.factory.connection()) {
			PreparedStatement statement = connection.prepareStatement(STATEMENT_SELECT_BY_MINECRAFT_IDENTIFIER);
			statement.setBytes(1, uuidToBytes(minecraftIdentifier));
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}

			String minecraftUsername = resultSet.getString("minecraft_username");
			MinecraftProfile minecraftProfile = new MinecraftProfile(minecraftIdentifier, minecraftUsername);
			Data data = new Data(minecraftProfile);

			String discordIdentifierAsString = resultSet.getString("discord_identifier");
			if (discordIdentifierAsString == null) {
				return data;
			}

			long discordIdentifier = Long.parseLong(discordIdentifierAsString);
			String discordUsername = resultSet.getString("discord_username");
			data.discordProfile(new DiscordProfile(discordIdentifier, discordUsername));
			return data;
		}
	}

	@Override
	public void upsert(Data data) throws SQLException {
		try (Connection connection = this.factory.connection()) {
			PreparedStatement statement = connection.prepareStatement(STATEMENT_UPSERT);
			statement.setBytes(1, uuidToBytes(data.minecraftProfile().identifier()));
			statement.setString(2, data.minecraftProfile().username());
			statement.setString(3, data.discordProfile().identifier().toString());
			statement.setString(4, data.discordProfile().username());
			statement.executeUpdate();
		}
	}

	//TODO Implementation
	@Override
	public boolean deleteByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException {
		return false;
	}

	//TODO Implementation
	@Override
	public int deleteByDiscordIdentifier(long discordIdentifier) throws SQLException {
		return 0;
	}

	private byte[] uuidToBytes(UUID uuid) {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
		return buffer.array();
	}

	private UUID bytesToUUID(byte[] bytes) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		long high = byteBuffer.getLong();
		long low = byteBuffer.getLong();
		return new UUID(high, low);
	}
}
