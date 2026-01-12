package fi.fabianadrian.discordwhitelist.velocity.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import fi.fabianadrian.discordwhitelist.velocity.DiscordWhitelistVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

public final class LoginListener {
	private static final TranslatableComponent COMPONENT_LOGIN_DENIED = Component.translatable(
			"discordwhitelist.login.denied"
	);
	private final DiscordWhitelistVelocity plugin;

	public LoginListener(DiscordWhitelistVelocity plugin) {
		this.plugin = plugin;
	}

	@Subscribe
	public void onJoin(LoginEvent event) {
		Player player = event.getPlayer();
		this.plugin.logger().info("player locale is  {}", player.getEffectiveLocale());
		if (this.plugin.discordWhitelist().storageManager().findByMinecraftIdentifier(player.getUniqueId()).join() != null) {
			return;
		}
		event.setResult(ResultedEvent.ComponentResult.denied(COMPONENT_LOGIN_DENIED));
	}
}
