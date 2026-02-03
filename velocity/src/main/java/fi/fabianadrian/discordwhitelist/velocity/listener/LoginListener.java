package fi.fabianadrian.discordwhitelist.velocity.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import fi.fabianadrian.discordwhitelist.velocity.DiscordWhitelistVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;

public final class LoginListener {
	private static final TranslatableComponent COMPONENT_LOGIN_DENIED = Component.translatable(
			"discordwhitelist.login.denied"
	);
	private static final String PERMISSION_BYPASS = "discordwhitelist.bypass";
	private final DiscordWhitelistVelocity plugin;

	public LoginListener(DiscordWhitelistVelocity plugin) {
		this.plugin = plugin;
	}

	@Subscribe
	public void onJoin(LoginEvent event) {
		Player player = event.getPlayer();

		if (player.hasPermission(PERMISSION_BYPASS)) {
			return;
		}

		if (this.plugin.discordWhitelist().dataManager().findByMinecraftIdentifier(player.getUniqueId()).join() != null) {
			return;
		}

		Component component = GlobalTranslator.render(COMPONENT_LOGIN_DENIED, this.plugin.discordWhitelist().config().defaultLocale());
		event.setResult(ResultedEvent.ComponentResult.denied(component));
	}
}
