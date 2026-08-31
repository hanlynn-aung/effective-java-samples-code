package chapter2.item1.good;

import java.util.Objects;

public final class GoodStaticFactoryService {
    private final String endpoint;

    private GoodStaticFactoryService(String endpoint) {
        this.endpoint = endpoint;
    }

    public static GoodStaticFactoryService connectedTo(String endpoint) {
        return new GoodStaticFactoryService(Objects.requireNonNull(endpoint, "endpoint"));
    }

    public String endpoint() {
        return endpoint;
    }
}
