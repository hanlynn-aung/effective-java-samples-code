package chapter2.item8;

import chapter2.item8.good.GoodAutoCloseableResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AutoCloseableTest {

    @Test
    @DisplayName("Good: using a closed resource fails loudly")
    void useAfterCloseThrows() {
        GoodAutoCloseableResource resource = new GoodAutoCloseableResource();
        resource.close();
        assertThrows(IllegalStateException.class, resource::use);
    }

    @Test
    @DisplayName("Good: try-with-resources closes the resource automatically")
    void tryWithResourcesClosesAutomatically() {
        GoodAutoCloseableResource resource = new GoodAutoCloseableResource();
        try (resource) {
            resource.use();
        }
        assertThrows(IllegalStateException.class, resource::use);
    }
}