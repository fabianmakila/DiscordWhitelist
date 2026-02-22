package fi.fabianadrian.discordwhitelist.common.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.config.section.StorageSection;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import fi.fabianadrian.discordwhitelist.common.profile.DiscordProfile;
import fi.fabianadrian.discordwhitelist.common.profile.MinecraftProfile;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class MariaDBStorage implements Storage {
	private static final String STATEMENT_UPSERT_DISCORD = """
			INSERT INTO dw_discord_profiles (
				discord_identifier,
				discord_username
			)
			VALUES (?, ?)
			ON DUPLICATE KEY UPDATE
				discord_username = VALUES(discord_username)
			""";
	private static final String STATEMENT_UPSERT_MINECRAFT = """
			INSERT INTO dw_minecraft_profiles (
				minecraft_identifier,
				minecraft_username,
				discord_identifier
			)
			VALUES (?, ?, ?)
			ON DUPLICATE KEY UPDATE
				minecraft_username = VALUES(minecraft_username),
				discord_identifier = VALUES(discord_identifier)
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
	private final HikariDataSource source;
	private final Flyway flyway;

	public MariaDBStorage(DiscordWhitelist discordWhitelist) throws SQLException {
		StorageSection config = discordWhitelist.config().storage();

		MariaDbDataSource ds = new MariaDbDataSource();
		ds.setUrl(config.url());
		ds.setUser(config.username());
		ds.setPassword(config.password());

		HikariConfig hikari = new HikariConfig();
		hikari.setDataSource(ds);
		hikari.setAutoCommit(true);

		this.source = new HikariDataSource(hikari);

		this.flyway = Flyway.configure(getClass().getClassLoader())
				.dataSource(this.source)
				.locations("classpath:db/migration/mariadb")
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
					UUID minecraftIdentifier = UUID.fromString(resultSet.getString("minecraft_identifier"));

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
			statement.setString(1, minecraftIdentifier.toString());

			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}

				String minecraftUsername = resultSet.getString("minecraft_username");
				MinecraftProfile minecraftProfile = new MinecraftProfile(minecraftIdentifier, minecraftUsername);
				Data data = new Data(minecraftProfile);

				long discordIdentifier = resultSet.getLong("discord_identifier");
				if (discordIdentifier == 0) {
					return data;
				}

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
			minecraftStatement.setString(1, data.minecraftProfile().identifier().toString());
			minecraftStatement.setString(2, data.minecraftProfile().username());

			if (data.discordProfile() != null) {
				PreparedStatement discordStatement = connection.prepareStatement(STATEMENT_UPSERT_DISCORD);
				discordStatement.setLong(1, data.discordProfile().identifier());
				discordStatement.setString(2, data.discordProfile().username());
				discordStatement.executeUpdate();

				minecraftStatement.setLong(3, data.discordProfile().identifier());
			} else {
				minecraftStatement.setNull(3, Types.BIGINT);
			}

			minecraftStatement.executeUpdate();
			connection.commit();
		}
	}

	@Override
	public boolean deleteByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException {
		return false;
	}

	@Override
	public int deleteByDiscordIdentifier(long discordIdentifier) throws SQLException {
		try (Connection connection = this.source.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(STATEMENT_DELETE_BY_DISCORD_IDENTIFIER);
			statement.setLong(1, discordIdentifier);
			return statement.executeUpdate();
		}
	}

	@Override
	public void close() {
		this.source.close();
	}
}
