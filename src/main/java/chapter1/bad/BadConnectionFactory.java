package chapter1.bad;

public final class BadConnectionFactory {
    public Connection open(String address) {
        return new Connection(address);
    }

    public static final class Connection {
        private final String address;

        public Connection(String address) {
            this.address = address;
        }

        public String address() {
            return address;
        }
    }
}
