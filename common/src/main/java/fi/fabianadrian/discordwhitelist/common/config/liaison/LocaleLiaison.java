package fi.fabianadrian.discordwhitelist.common.config.liaison;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import space.arim.dazzleconf.LoadResult;
import space.arim.dazzleconf.engine.*;
import space.arim.dazzleconf.reflect.TypeToken;

import java.util.Locale;

public final class LocaleLiaison implements TypeLiaison {
	@Override
	public @Nullable <V> Agent<V> makeAgent(@NonNull TypeToken<V> typeToken, @NonNull Handshake handshake) {
		return Agent.matchOnToken(typeToken, Locale.class, LocaleAgent::new);
	}

	private static class LocaleAgent implements Agent<Locale> {

		@Override
		public @Nullable DefaultValues<Locale> loadDefaultValues(@NonNull DefaultInit<Locale> defaultInit) {
			return Agent.super.loadDefaultValues(defaultInit);
		}

		@Override
		public @NonNull SerializeDeserialize<Locale> makeSerializer() {
			return new SerializeDeserialize<>() {

				@Override
				public @NonNull LoadResult<Locale> deserialize(@NonNull DeserializeInput deser) {
					LoadResult<String> result = deser.requireString();
					if (result.isFailure()) {
						return LoadResult.failure(result.getErrorContexts());
					}
					return LoadResult.of(Locale.forLanguageTag(result.getOrThrow()));
				}

				@Override
				public void serialize(@NotNull Locale value, @NonNull SerializeOutput ser) {
					ser.outString(value.toLanguageTag());
				}
			};
		}
	}
}
