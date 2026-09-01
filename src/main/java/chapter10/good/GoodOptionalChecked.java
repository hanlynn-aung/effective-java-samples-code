package chapter10.good;

public final class GoodOptionalChecked {

    private final java.util.HashMap<String, String> store = new java.util.HashMap<>();

    public void put(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        return store.get(key);
    }

    public boolean contains(String key) {
        return store.containsKey(key);
    }

    public int size() {
        return store.size();
    }
}