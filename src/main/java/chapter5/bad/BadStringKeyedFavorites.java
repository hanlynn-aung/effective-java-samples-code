package chapter5.bad;

import java.util.HashMap;
import java.util.Map;

public final class BadStringKeyedFavorites {
    private final Map<String, Object> favorites = new HashMap<>();

    public void put(String key, Object value) {
        favorites.put(key, value);
    }

    public Object get(String key) {
        return favorites.get(key);
    }
}