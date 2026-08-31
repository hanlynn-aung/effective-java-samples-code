package chapter8;

import chapter8.bad.BadReload;
import chapter8.good.GoodDispatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverloadTest {

    @Test
    @DisplayName("Bad: overload resolution is compile-time, surprising with a Collection reference")
    void badOverloadResolvesToCollection() {
        BadReload bad = new BadReload();
        List<String> actualList = new ArrayList<>();
        Set<String> actualSet = new HashSet<>();
        assertEquals("list", bad.classify(actualList));
        assertEquals("set", bad.classify(actualSet));

        // A List typed as Collection picks the Collection overload - often not what's wanted.
        Collection<String> generic = actualList;
        assertEquals("collection", bad.classify(generic));
    }

    @Test
    @DisplayName("Good: distinct method names remove the surprise")
    void goodDistinctNamesAreExplicit() {
        GoodDispatch good = new GoodDispatch();
        List<String> list = new ArrayList<>();
        assertEquals("list", good.classifyAsList(list));
    }
}