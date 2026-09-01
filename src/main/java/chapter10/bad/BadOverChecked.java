package chapter10.bad;

public final class BadOverChecked {

    public static final class StorageException extends Exception {
        public StorageException(String message) {
            super(message);
        }
    }

    private final java.util.HashMap<String, String> store = new java.util.HashMap<>();

    public void put(String key, String value) throws StorageException {
        store.put(key, value);
    }

    public String get(String key) throws StorageException {
        return store.get(key);
    }

    public boolean contains(String key) throws StorageException {
        return store.containsKey(key);
    }

    public int size() throws StorageException {
        return store.size();
    }
}