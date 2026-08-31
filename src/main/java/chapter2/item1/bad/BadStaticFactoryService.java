package chapter2.item1.bad;

public final class BadStaticFactoryService {
    private final String endpoint;

    public BadStaticFactoryService(String endpoint) {
        this.endpoint = endpoint;
    }

    public String endpoint() {
        return endpoint;
    }
}
