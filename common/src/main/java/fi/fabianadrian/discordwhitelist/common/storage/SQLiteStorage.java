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
	private final SQLiteDataSource source;
	private final Flyway flyway;

	public SQLiteStorage(DiscordWhitelist discordWhitelist) {
		SQLiteDataSource source = new SQLiteDataSource();
		source.setUrl("jdbc:sqlite:" + discordWhitelist.dataDirectory().resolve("database.db").toAbsolutePath());
		this.source = source;

		this.flyway = Flyway.configure()
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
		try (Connection connection = this.source.getConnection()) {
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
		try (Connection connection = this.source.getConnection()) {
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
