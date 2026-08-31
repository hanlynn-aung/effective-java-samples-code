package chapter2.item1;

import chapter2.item1.bad.BadStaticFactoryService;
import chapter2.item1.good.GoodStaticFactoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceFactoryTest {

    @Test
    @DisplayName("Bad: public constructor accepts a null endpoint")
    void badAllowsNullEndpoint() {
        BadStaticFactoryService service = new BadStaticFactoryService(null);
        assertNull(service.endpoint());
    }

    @Test
    @DisplayName("Good: named static factory rejects null endpoint immediately")
    void goodRejectsNullEndpoint() {
        assertThrows(NullPointerException.class,
                () -> GoodStaticFactoryService.connectedTo(null));
    }

    @Test
    @DisplayName("Good: named static factory keeps the endpoint")
    void goodKeepsEndpoint() {
        GoodStaticFactoryService service =
                GoodStaticFactoryService.connectedTo("https://api.example.com");
        assertEquals("https://api.example.com", service.endpoint());
    }
}