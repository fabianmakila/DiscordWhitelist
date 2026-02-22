package fi.fabianadrian.discordwhitelist.common.profile;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.object.ObjectContents;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public final class MinecraftProfile extends Profile<UUID> {
	public MinecraftProfile(UUID id, String username) {
		super(id, username);
	}

	@Override
	public Component asComponent() {
		return Component.textOfChildren(
						Component.object(ObjectContents.playerHead(super.identifier)),
						Component.space(),
						text(super.username)
				).hoverEvent(HoverEvent.showText(Component.text(super.identifier.toString())))
				.clickEvent(ClickEvent.copyToClipboard(super.identifier.toString()));
	}
}
