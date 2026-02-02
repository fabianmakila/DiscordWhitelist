package fi.fabianadrian.discordwhitelist.paper.listener;

import fi.fabianadrian.discordwhitelist.paper.DiscordWhitelistPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class JoinListener implements Listener {
	private static final TranslatableComponent COMPONENT_LOGIN_DENIED = Component.translatable(
			"discordwhitelist.login.denied"
	);
	private static final String PERMISSION_BYPASS = "discordwhitelist.bypass";
	private final DiscordWhitelistPaper plugin;

	public JoinListener(DiscordWhitelistPaper plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (player.hasPermission(PERMISSION_BYPASS)) {
			return;
		}

		if (this.plugin.discordWhitelist().storageManager().findByMinecraftIdentifier(player.getUniqueId()).join() != null) {
			return;
		}

		Component component = GlobalTranslator.render(COMPONENT_LOGIN_DENIED, this.plugin.discordWhitelist().config().defaultLocale());
		event.getPlayer().kick(component);
	}
}
