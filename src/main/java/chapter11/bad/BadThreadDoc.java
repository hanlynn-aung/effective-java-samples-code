package chapter11.bad;

import java.util.HashMap;
import java.util.Map;

/**
 * (bad) A cache backed by a plain HashMap with NO thread-safety statement.
 * Concurrent access corrupts it silently.
 */
public final class BadThreadDoc {

    private final Map<String, Object> cache = new HashMap<>();

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public Object get(String key) {
        return cache.get(key);
    }
}