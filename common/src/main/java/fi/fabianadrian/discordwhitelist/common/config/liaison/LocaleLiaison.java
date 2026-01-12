package fi.fabianadrian.discordwhitelist.common.config.liaison;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.arim.dazzleconf.engine.TypeLiaison;
import space.arim.dazzleconf.reflect.TypeToken;

public class LocaleLiaison implements TypeLiaison {
	@Override
	public @Nullable <V> Agent<V> makeAgent(@NonNull TypeToken<V> typeToken, @NonNull Handshake handshake) {
		return null;
	}
}
