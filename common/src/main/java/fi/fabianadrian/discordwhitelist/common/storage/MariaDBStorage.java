package fi.fabianadrian.discordwhitelist.common.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fi.fabianadrian.discordwhitelist.common.DiscordWhitelist;
import fi.fabianadrian.discordwhitelist.common.config.section.StorageSection;
import fi.fabianadrian.discordwhitelist.common.data.Data;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class MariaDBStorage implements Storage {
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

		this.flyway = Flyway.configure()
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
		return List.of();
	}

	@Override
	public Data selectByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException {
		return null;
	}

	@Override
	public void upsert(Data data) throws SQLException {

	}

	@Override
	public boolean deleteByMinecraftIdentifier(UUID minecraftIdentifier) throws SQLException {
		return false;
	}

	@Override
	public int deleteByDiscordIdentifier(long discordIdentifier) throws SQLException {
		return 0;
	}

	@Override
	public void close() {
		this.source.close();
	}
}
