package chapter2.item7.good;

import java.util.Map;
import java.util.WeakHashMap;

public final class GoodWeakCache {
    private final Map<String, byte[]> entries = new WeakHashMap<>();

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