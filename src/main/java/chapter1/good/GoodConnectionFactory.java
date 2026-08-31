package chapter1.good;

import java.util.Objects;

public final class GoodConnectionFactory {
    public Connection open(String address) {
        return Connection.to(address);
    }

    public static final class Connection {
        private final String address;

        private Connection(String address) {
            this.address = address;
        }

        public static Connection to(String address) {
            return new Connection(Objects.requireNonNull(address, "address"));
        }

        public String address() {
            return address;
        }
    }
}
