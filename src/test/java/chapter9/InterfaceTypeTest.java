package chapter9;

import chapter9.bad.BadConcreteType;
import chapter9.good.GoodInterfaceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterfaceTypeTest {

    @Test
    @DisplayName("Bad: concrete ArrayList/HashMap params lock callers into one implementation")
    void badConcreteTypeLocksImpl() {
        BadConcreteType bad = new BadConcreteType(
                new ArrayList<>(), new HashMap<>());
        bad.add("a");
        bad.add("b");
        assertEquals(2, bad.names().size());
        // Callers cannot pass a LinkedList/TreeMap without changing the API.
    }

    @Test
    @DisplayName("Good: interface params accept any impl - swap ArrayList for LinkedList with no API change")
    void goodInterfaceAcceptsAnyImpl() {
        List<String> names = new LinkedList<>();
        Map<String, Integer> scores = new TreeMap<>();
        GoodInterfaceType good = new GoodInterfaceType(names, scores);
        good.add("a");
        assertEquals(1, good.names().size());
        assertEquals(0, good.score("a"));
    }

    @Test
    @DisplayName("Both: behaviour is stable regardless of backing impl")
    void bothStable() {
        assertEquals(2,
                new BadConcreteType(new ArrayList<>(List.of("x", "y")), new HashMap<>())
                        .names().size());
    }
}