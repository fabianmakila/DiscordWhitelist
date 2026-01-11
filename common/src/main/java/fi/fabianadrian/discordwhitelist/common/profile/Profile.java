package fi.fabianadrian.discordwhitelist.common.profile;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import static net.kyori.adventure.text.Component.text;

public abstract class Profile<T> {
	protected final T identifier;
	protected String username;

	public Profile(T identifier, String username) {
		this.identifier = identifier;
		this.username = username;
	}

	public T identifier() {
		return identifier;
	}

	public String username() {
		return username;
	}

	public void username(String username) {
		this.username = username;
	}

	public Component asComponent() {
		return text()
				.content(this.username)
				.hoverEvent(HoverEvent.showText(text(this.identifier.toString())))
				.clickEvent(ClickEvent.copyToClipboard(this.identifier.toString()))
				.build();
	}
}
