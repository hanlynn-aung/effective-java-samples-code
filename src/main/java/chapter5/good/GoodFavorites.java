package chapter5.good;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class GoodFavorites {
    private final Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> void put(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }

    public <T> T get(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}