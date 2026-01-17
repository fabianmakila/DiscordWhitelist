package fi.fabianadrian.discordwhitelist.common.config.section;

import fi.fabianadrian.discordwhitelist.common.storage.StorageType;

public interface StorageSection {
	default StorageType type() {
		return StorageType.SQLITE;
	}

	default String url() {
		return "jdbc:mariadb://host:3306/database";
	}

	default String username() {
		return "username";
	}

	default String password() {
		return "password";
	}
}
