package fi.fabianadrian.discordwhitelist.common.data;

import fi.fabianadrian.discordwhitelist.common.profile.DiscordProfile;
import fi.fabianadrian.discordwhitelist.common.profile.minecraft.MinecraftProfile;

public final class Data {
	private final MinecraftProfile minecraftProfile;
	private DiscordProfile discordProfile;

	public Data(MinecraftProfile profile) {
		this.minecraftProfile = profile;
	}

	public void discordProfile(DiscordProfile profile) {
		this.discordProfile = profile;
	}

	public MinecraftProfile minecraftProfile() {
		return this.minecraftProfile;
	}

	public DiscordProfile discordProfile() {
		return this.discordProfile;
	}
}
