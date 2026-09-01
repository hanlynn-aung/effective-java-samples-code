package chapter10;

import chapter10.bad.BadOverChecked;
import chapter10.good.GoodOptionalChecked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckedOveruseTest {

    @Test
    @DisplayName("Bad: every trivial accessor must declare a checked exception")
    void badCheckedEverywhere() throws BadOverChecked.StorageException {
        BadOverChecked bad = new BadOverChecked();
        bad.put("k", "v");
        assertTrue(bad.contains("k"));
        assertEquals("v", bad.get("k"));
        assertEquals(1, bad.size());
    }

    @Test
    @DisplayName("Good: no checked exceptions on operations that cannot meaningfully fail")
    void goodFreeOfChecked() {
        GoodOptionalChecked good = new GoodOptionalChecked();
        good.put("k", "v");
        assertTrue(good.contains("k"));
        assertEquals("v", good.get("k"));
        assertEquals(1, good.size());
    }
}