CREATE TABLE IF NOT EXISTS dw_discord_profiles
(
    discord_identifier TEXT PRIMARY KEY,
    discord_username   TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS dw_minecraft_profiles
(
    minecraft_identifier BLOB PRIMARY KEY,
    minecraft_username   TEXT NOT NULL,
    discord_identifier   TEXT,
    FOREIGN KEY (discord_identifier)
        REFERENCES dw_discord_profiles (discord_identifier)
        ON DELETE SET NULL
);


