package fi.fabianadrian.discordwhitelist.common.command.minecraft;

import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionProvider;
import org.jspecify.annotations.NonNull;

public final class MinecraftCaptionProvider implements CaptionProvider<Audience> {
	@Override
	public @NonNull String provide(@NonNull Caption caption, @NonNull Audience recipient) {
		return "discordwhitelist.command.minecraft." + caption.key();
	}
}
