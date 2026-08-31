package chapter5.bad;

import java.util.List;

public final class BadHeapPollution {

    public static void dangerous(List<String>... stringLists) {
        Object[] array = stringLists;
        array[0] = List.of(42);
        String first = stringLists[0].get(0);
        System.out.println("first string was " + first);
    }
}