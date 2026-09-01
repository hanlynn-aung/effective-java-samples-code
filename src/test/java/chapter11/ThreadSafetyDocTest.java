package chapter11;

import chapter11.bad.BadThreadDoc;
import chapter11.good.GoodThreadDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadSafetyDocTest {

    @Test
    @DisplayName("Bad: a HashMap-backed cache documents nothing about thread safety")
    void badDocumentsNothing() {
        BadThreadDoc bad = new BadThreadDoc();
        bad.put("k", "v");
        assertEquals("v", bad.get("k"));
        // No Javadoc statement on thread safety -> caller cannot know to synchronize.
    }

    @Test
    @DisplayName("Good: the doc states thread-safe behaviour and what callers must still guard")
    void goodDocumentsThreadSafety() {
        GoodThreadDoc good = new GoodThreadDoc();
        good.put("k", "v");
        assertEquals("v", good.get("k"));
        // putIfAbsent is atomic and safe to call concurrently; the existing
        // key wins, so it returns false (no new value inserted).
        assertEquals(false, good.putIfAbsent("k", "other"));
        assertEquals("v", good.get("k"));

        // An absent key is inserted atomically.
        assertEquals(true, good.putIfAbsent("new", "first"));
        assertEquals("first", good.get("new"));
    }
}