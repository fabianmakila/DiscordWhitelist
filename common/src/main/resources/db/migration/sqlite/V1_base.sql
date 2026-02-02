CREATE TABLE IF NOT EXISTS dw_data (
    minecraft_identifier BLOB PRIMARY KEY,
    minecraft_username  TEXT,
    discord_identifier TEXT,
    discord_username TEXT
);