package chapter1.good;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class GoodCachedConnections {
    private static final Map<String, Connection> CACHE = new ConcurrentHashMap<>();

    public interface Connection {
        String address();
    }

    private static final class CachedConnection implements Connection {
        private final String address;

        CachedConnection(String address) {
            this.address = address;
        }

        @Override
        public String address() {
            return address;
        }
    }

    public static Connection to(String address) {
        Objects.requireNonNull(address, "address");
        return CACHE.computeIfAbsent(address, CachedConnection::new);
    }
}