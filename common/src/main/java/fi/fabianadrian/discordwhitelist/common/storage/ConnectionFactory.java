package fi.fabianadrian.discordwhitelist.common.storage;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {
	Connection connection() throws SQLException;
}
