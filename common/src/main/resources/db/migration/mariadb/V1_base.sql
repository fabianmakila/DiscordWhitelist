CREATE TABLE IF NOT EXISTS dw_data (
    minecraft_identifier UUID PRIMARY KEY,
    minecraft_username  VARCHAR(16) NOT NULL,
    discord_identifier BIGINT,
    discord_username VARCHAR(32)
);