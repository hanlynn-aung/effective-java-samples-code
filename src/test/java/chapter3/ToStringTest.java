package chapter3;

import chapter3.bad.BadPoint;
import chapter3.good.GoodPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToStringTest {

    @Test
    @DisplayName("Bad: default toString carries no useful information")
    void badDefaultToStringHasNoFieldData() {
        BadPoint point = new BadPoint();
        point.x = 1;
        point.y = 2;
        String text = point.toString();
        assertTrue(text.matches("chapter3\\.bad\\.BadPoint@[0-9a-f]+"));
        assertFalse(text.contains("x="));
    }

    @Test
    @DisplayName("Good: toString shows every field")
    void goodToStringShowsFields() {
        assertEquals("GoodPoint[x=1, y=2]", new GoodPoint(1, 2).toString());
    }
}