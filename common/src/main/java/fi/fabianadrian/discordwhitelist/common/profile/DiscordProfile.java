package fi.fabianadrian.discordwhitelist.common.profile;

import net.dv8tion.jda.api.entities.User;

public final class DiscordProfile extends Profile<Long> {
	public DiscordProfile(Long id, String username) {
		super(id, username);
	}

	public DiscordProfile(User user) {
		super(user.getIdLong(), user.getName());
	}
}
