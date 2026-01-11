package fi.fabianadrian.discordwhitelist.common.storage.sqlite;

import fi.fabianadrian.discordwhitelist.common.storage.ConnectionFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLiteConnectionFactory implements ConnectionFactory {
	private final String url;

	public SQLiteConnectionFactory(Path dataDirectory) {
		this.url = "jdbc:sqlite:" + dataDirectory.toAbsolutePath();
	}

	@Override
	public Connection connection() throws SQLException {
		return DriverManager.getConnection(this.url);
	}
}
