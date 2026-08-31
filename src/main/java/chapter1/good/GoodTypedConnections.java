package chapter1.good;

import java.util.Objects;

public final class GoodTypedConnections {
    public interface Connection {
        String address();
        boolean secure();
    }

    private static final class PlainConnection implements Connection {
        private final String address;

        PlainConnection(String address) {
            this.address = address;
        }

        @Override
        public String address() {
            return address;
        }

        @Override
        public boolean secure() {
            return false;
        }
    }

    private static final class SecureConnection implements Connection {
        private final String address;

        SecureConnection(String address) {
            this.address = address;
        }

        @Override
        public String address() {
            return address;
        }

        @Override
        public boolean secure() {
            return true;
        }
    }

    public static Connection plain(String address) {
        return new PlainConnection(Objects.requireNonNull(address, "address"));
    }

    public static Connection secure(String address) {
        return new SecureConnection(Objects.requireNonNull(address, "address"));
    }
}