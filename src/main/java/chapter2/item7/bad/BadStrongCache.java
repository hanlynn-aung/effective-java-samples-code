package chapter2.item7.bad;

import java.util.HashMap;
import java.util.Map;

public final class BadStrongCache {
    private final Map<String, byte[]> entries = new HashMap<>();

    public void put(String key, byte[] value) {
        entries.put(key, value);
    }

    public byte[] get(String key) {
        return entries.get(key);
    }

    public int size() {
        return entries.size();
    }
}