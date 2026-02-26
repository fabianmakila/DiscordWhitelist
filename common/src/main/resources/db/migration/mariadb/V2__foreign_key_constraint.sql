ALTER TABLE dw_minecraft_profiles
    DROP FOREIGN KEY dw_minecraft_profiles_ibfk_1;

ALTER TABLE dw_minecraft_profiles
    ADD CONSTRAINT fk_dw_minecraft_profiles_dw_discord_profiles_discord_identifier
        FOREIGN KEY (discord_identifier)
            REFERENCES dw_discord_profiles(discord_identifier)
            ON DELETE SET NULL;