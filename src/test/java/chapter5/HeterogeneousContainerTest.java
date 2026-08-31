package chapter5;

import chapter5.bad.BadStringKeyedFavorites;
import chapter5.good.GoodFavorites;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeterogeneousContainerTest {

    @Test
    @DisplayName("Bad: string keys force callers to cast and can return surprises")
    void badStringKeysForceCasts() {
        BadStringKeyedFavorites favorites = new BadStringKeyedFavorites();
        favorites.put("favorite number", 42);
        assertEquals(42, favorites.get("favorite number"));
        assertThrows(ClassCastException.class,
                () -> { String wrecked = (String) favorites.get("favorite number"); });
    }

    @Test
    @DisplayName("Good: (Class<T>, T) keeps every value's type with the key")
    void goodFavoritesArePerTypeSafe() {
        GoodFavorites favorites = new GoodFavorites();
        favorites.put(String.class, "java");
        favorites.put(Integer.class, 42);
        favorites.put(Class.class, GoodFavorites.class);

        assertEquals("java", favorites.get(String.class));
        assertEquals(42, favorites.get(Integer.class));
        assertEquals(GoodFavorites.class, favorites.get(Class.class));
    }

    @Test
    @DisplayName("Good: missing entries return null instead of casting garbage")
    void goodMissingKeyReturnsNull() {
        GoodFavorites favorites = new GoodFavorites();
        assertNull(favorites.get(String.class));
    }

    @Test
    @DisplayName("Good: the runtime cast inside put rejects wrong-typed values via raw access")
    void goodPutEnforcesAtRuntime() {
        GoodFavorites favorites = new GoodFavorites();
        assertThrows(ClassCastException.class,
                () -> favorites.put((Class) String.class, Integer.valueOf(1)));
    }
}