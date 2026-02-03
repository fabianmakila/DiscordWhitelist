CREATE TABLE IF NOT EXISTS dw_discord_profiles
(
    discord_identifier BIGINT PRIMARY KEY,
    discord_username   VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS dw_minecraft_profiles
(
    minecraft_identifier UUID PRIMARY KEY,
    minecraft_username   VARCHAR(16) NOT NULL,
    discord_identifier   BIGINT,
    FOREIGN KEY (discord_identifier)
        REFERENCES dw_discord_profiles (discord_identifier)
        ON DELETE SET NULL
);