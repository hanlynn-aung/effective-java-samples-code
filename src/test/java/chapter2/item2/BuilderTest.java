package chapter2.item2;

import chapter2.item2.bad.BadBeanPizza;
import chapter2.item2.bad.BadTelescopingUser;
import chapter2.item2.good.GoodBuilderUser;
import chapter2.item2.good.GoodCalzone;
import chapter2.item2.good.GoodNyPizza;
import chapter2.item2.good.GoodPizza;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuilderTest {

    @Test
    @DisplayName("Bad: same-type telescoping args can be swapped silently")
    void telescopingArgsCanBeSwappedSilently() {
        BadTelescopingUser user = new BadTelescopingUser(
                "Han", "123-456", "han@example.com");
        assertEquals("123-456", user.email());
        assertEquals("han@example.com", user.phone());
    }

    @Test
    @DisplayName("Bad: JavaBeans object is mutable after 'construction'")
    void javaBeansIsMutable() {
        BadBeanPizza pizza = new BadBeanPizza();
        assertTrue(pizza.cheese());
        pizza.setCheese(false);
        assertFalse(pizza.cheese());
    }

    @Test
    @DisplayName("Good: builder rejects missing required field")
    void builderRejectsMissingRequiredField() {
        assertThrows(NullPointerException.class,
                () -> GoodBuilderUser.builder(null).build());
    }

    @Test
    @DisplayName("Good: builder applies default values for untouched fields")
    void builderAppliesDefaults() {
        GoodBuilderUser user = GoodBuilderUser.builder("Han").build();
        assertEquals("Han", user.name());
        assertNull(user.email());
        assertNull(user.phone());
        assertFalse(user.admin());
    }

    @Test
    @DisplayName("Good: builder builds a fully configured immutable object")
    void builderBuildsConfiguredObject() {
        GoodBuilderUser user = GoodBuilderUser.builder("Han")
                .email("han@example.com")
                .phone("123-456")
                .admin(true)
                .build();
        assertEquals("Han", user.name());
        assertEquals("han@example.com", user.email());
        assertEquals("123-456", user.phone());
        assertTrue(user.admin());
    }

    @Test
    @DisplayName("Good: hierarchical builder keeps the most specific type")
    void hierarchicalBuilderKeepsSpecificType() {
        GoodNyPizza ny = new GoodNyPizza.Builder(GoodNyPizza.Size.LARGE)
                .addTopping(GoodPizza.Topping.HAM)
                .addTopping(GoodPizza.Topping.ONION)
                .build();
        assertEquals(GoodNyPizza.Size.LARGE, ny.size());
        assertEquals(List.of(GoodPizza.Topping.HAM, GoodPizza.Topping.ONION),
                ny.toppings());
    }

    @Test
    @DisplayName("Good: calzone builder exposes its own flag")
    void calzoneBuilderExposesOwnFlag() {
        GoodCalzone calzone = new GoodCalzone.Builder()
                .addTopping(GoodPizza.Topping.SAUSAGE)
                .sauceInside()
                .build();
        assertTrue(calzone.sauceInside());
        assertEquals(List.of(GoodPizza.Topping.SAUSAGE), calzone.toppings());
    }
}