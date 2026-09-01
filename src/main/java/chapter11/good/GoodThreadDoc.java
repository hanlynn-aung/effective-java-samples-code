package chapter11.good;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thread-safe cache.
 *
 * <p><b>Thread safety:</b> instances are <em>thread-safe</em>. Compound
 * read-modify-write sequences (check-then-act, read-then-write) are
 * <em>not</em> atomic and must be synchronized by callers, or done via the
 * atomic {@link ConcurrentMap} operations such as {@code putIfAbsent}.
 *
 * <p>All public operations are safe to call concurrently from multiple threads
 * without external synchronization.
 */
public final class GoodThreadDoc {

    private final ConcurrentMap<String, Object> cache = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public boolean putIfAbsent(String key, Object value) {
        return cache.putIfAbsent(key, value) == null;
    }
}