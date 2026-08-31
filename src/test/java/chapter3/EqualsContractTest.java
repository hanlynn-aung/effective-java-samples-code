package chapter3;

import chapter3.bad.BadCaseInsensitiveString;
import chapter3.bad.BadMutablePoint;
import chapter3.bad.BadPoint;
import chapter3.bad.BadTransitivityColorPoint;
import chapter3.bad.BadTransitivityPoint;
import chapter3.good.GoodPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EqualsContractTest {

    @Test
    @DisplayName("Bad: identity equals ignores values entirely")
    void badIdentityEqualsIgnoresValues() {
        assertNotEquals(point(1, 2), point(1, 2));
    }

    @Test
    @DisplayName("Good: value equals is reflexive")
    void goodEqualsIsReflexive() {
        GoodPoint point = new GoodPoint(1, 2);
        assertTrue(point.equals(point));
    }

    @Test
    @DisplayName("Good: value equals is symmetric")
    void goodEqualsIsSymmetric() {
        GoodPoint a = new GoodPoint(1, 2);
        GoodPoint b = new GoodPoint(1, 2);
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

    @Test
    @DisplayName("Good: value equals is transitive")
    void goodEqualsIsTransitive() {
        GoodPoint a = new GoodPoint(1, 2);
        GoodPoint b = new GoodPoint(1, 2);
        GoodPoint c = new GoodPoint(1, 2);
        assertTrue(a.equals(b));
        assertTrue(b.equals(c));
        assertTrue(a.equals(c));
    }

    @Test
    @DisplayName("Good: equals never treats non-null as null")
    void goodEqualsRejectsNull() {
        assertFalse(new GoodPoint(1, 2).equals(null));
    }

    @Test
    @DisplayName("Bad: String-comparing equals violates symmetry")
    void badCaseInsensitiveStringViolatesSymmetry() {
        BadCaseInsensitiveString cis = new BadCaseInsensitiveString("Foo");
        assertTrue(cis.equals("foo"));
        assertFalse("foo".equals(cis));
    }

    @Test
    @DisplayName("Bad: subclassed equals breaks transitivity")
    void badSubclassedEqualsBreaksTransitivity() {
        BadTransitivityPoint p = new BadTransitivityPoint(1, 2);
        BadTransitivityColorPoint red =
                new BadTransitivityColorPoint(1, 2, "red");
        BadTransitivityColorPoint blue =
                new BadTransitivityColorPoint(1, 2, "blue");
        assertTrue(p.equals(red));
        assertTrue(p.equals(blue));
        assertFalse(red.equals(blue));
    }

    @Test
    @DisplayName("Bad: mutating a value-equal object breaks HashSet lookups")
    void mutableValueObjectBreaksHashSet() {
        Set<BadMutablePoint> set = new HashSet<>();
        BadMutablePoint point = new BadMutablePoint(1, 2);
        set.add(point);
        assertTrue(set.contains(point));
        point.x = 99;
        assertFalse(set.contains(point));
        assertEquals(1, set.size());
    }

    private static BadPoint point(int x, int y) {
        BadPoint point = new BadPoint();
        point.x = x;
        point.y = y;
        return point;
    }
}