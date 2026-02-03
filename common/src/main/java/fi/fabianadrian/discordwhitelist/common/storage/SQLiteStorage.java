package fi.fabianadrian.discordwhitelist.common.storage;

import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.profile.DiscordProfile;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.sqlite.SQLiteDataSource;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class SQLiteStorage implements Storage {
	private static final String STATEMENT_UPSERT_DISCORD = """
			INSERT INTO dw_discord_profiles (
				discord_identifier,
				discord_username
			)
			VALUES (?, ?)
			ON CONFLICT(discord_identifier) DO UPDATE SET
				discord_username = excluded.discord_username
			""";
	private static final String STATEMENT_UPSERT_MINECRAFT = """
			INSERT INTO dw_minecraft_profiles (
				minecraft_identifier,
				minecraft_username,
				discord_identifier
			)
			VALUES (?, ?, ?)
			ON CONFLICT(minecraft_identifier) DO UPDATE SET
				minecraft_username = excluded.minecraft_username,
				discord_identifier = excluded.discord_identifier
			""";
	private static final String STATEMENT_SELECT_BY_MINECRAFT_IDENTIFIER = """
			SELECT
					m.minecraft_username,
					m.discord_identifier,
					d.discord_username
				FROM dw_minecraft_profiles m
				LEFT JOIN dw_discord_profiles d
					ON m.discord_identifier = d.discord_identifier
				WHERE m.minecraft_identifier = ?

			""";
	private static final String STATEMENT_SELECT_BY_DISCORD_IDENTIFIER = """
			SELECT
					m.minecraft_identifier,
					m.minecraft_username,
					d.discord_username
				FROM dw_minecraft_profiles m
				LEFT JOIN dw_discord_profiles d
					ON m.discord_identifier = d.discord_identifier
				WHERE m.discord_identifier = ?

			""";
	private static final String STATEMENT_DELETE_BY_DISCORD_IDENTIFIER = """
			DELETE FROM dw_discord_profiles
			WHERE discord_identifier = ?
			""";
	private final SQLiteDataSource source;
	private final Flyway flyway;

	public SQLiteStorage(DiscordWhitelist discordWhitelist) {
		SQLiteDataSource source = new SQLiteDataSource();
		source.setUrl("jdbc:sqlite:" + discordWhitelist.dataDirectory().resolve("database.db").toAbsolutePath());
		this.source = source;

		this.flyway = Flyway.configure(getClass().getClassLoader())
				.dataSource(this.source)
				.locations("classpath:db/migration/sqlite")
				.communityDBSupportEnabled(true)
				.load();
	}

	@Override
	public void migrate() throws FlywayException {
		this.flyway.migrate();
	}

	@Override
	public Collection<Data> selectByDiscordIdentifier(long discordIdentifier) throws SQLException {
		try (Connection connection = this.source.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(STATEMENT_SELECT_BY_DISCORD_IDENTIFIER);
			statement.setString(1, String.valueOf(discordIdentifier));

			List<Data> dataList = new ArrayList<>();
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					UUID minecraftIdentifier = bytesToUUID(resultSet.getBytes("minecraft_identifier"));

					String minecraftUsername = resultSet.getString("minecraft_username");
					MinecraftProfile minecraftProfile = new MinecraftProfile(minecraftIdentifier, minecraftUsername);
					Data data = new Data(minecraftProfile);

					String discordUsername = resultSet.getString("discord_username");
					data.discordProfile(new DiscordProfile(discordIdentifier, discordUsername));
					dataList.add(data);
				}
			}

			return dataList;
		}
	}

	@Override
	public Data selectByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException {
		try (Connection connection = this.source.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(STATEMENT_SELECT_BY_MINECRAFT_IDENTIFIER);
			statement.setBytes(1, uuidToBytes(minecraftIdentifier));

			try (ResultSet resultSet = statement.executeQuery()) {
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
	}

	@Override
	public void upsert(Data data) throws SQLException {
		try (Connection connection = this.source.getConnection()) {
			connection.setAutoCommit(false);

			PreparedStatement minecraftStatement = connection.prepareStatement(STATEMENT_UPSERT_MINECRAFT);
			minecraftStatement.setBytes(1, uuidToBytes(data.minecraftProfile().identifier()));
			minecraftStatement.setString(2, data.minecraftProfile().username());

			if (data.discordProfile() != null) {
				PreparedStatement discordStatement = connection.prepareStatement(STATEMENT_UPSERT_DISCORD);
				discordStatement.setString(1, data.discordProfile().identifier().toString());
				discordStatement.setString(2, data.discordProfile().username());
				discordStatement.executeUpdate();

				minecraftStatement.setString(3, data.discordProfile().identifier().toString());
			} else {
				minecraftStatement.setString(3, null);
			}

			minecraftStatement.executeUpdate();
			connection.commit();
		}
	}

	//TODO Implementation
	@Override
	public boolean deleteByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException {
		return false;
	}

	@Override
	public int deleteByDiscordIdentifier(long discordIdentifier) throws SQLException {
		try (Connection connection = this.source.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(STATEMENT_DELETE_BY_DISCORD_IDENTIFIER);
			statement.setString(1, String.valueOf(discordIdentifier));
			return statement.executeUpdate();
		}
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
