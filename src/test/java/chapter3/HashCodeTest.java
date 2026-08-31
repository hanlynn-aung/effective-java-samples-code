package chapter3;

import chapter3.bad.BadHashPoint;
import chapter3.good.GoodPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashCodeTest {

    @Test
    @DisplayName("Bad: equal objects produce different hashCodes")
    void badEqualObjectsHaveDifferentHashes() {
        BadHashPoint a = new BadHashPoint(1, 2);
        BadHashPoint b = new BadHashPoint(1, 2);
        assertTrue(a.equals(b));
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Bad: different hashes allow duplicates into a HashSet")
    void badHashSetAllowsDuplicates() {
        Set<BadHashPoint> set = new HashSet<>();
        set.add(new BadHashPoint(1, 2));
        set.add(new BadHashPoint(1, 2));
        assertEquals(2, set.size());
    }

    @Test
    @DisplayName("Good: equal objects share an equal (cached) hashCode")
    void goodEqualObjectsShareHash() {
        GoodPoint a = new GoodPoint(1, 2);
        GoodPoint b = new GoodPoint(1, 2);
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Good: HashSet deduplicates equal elements")
    void goodHashSetDeduplicates() {
        Set<GoodPoint> set = new HashSet<>();
        set.add(new GoodPoint(1, 2));
        set.add(new GoodPoint(1, 2));
        assertEquals(1, set.size());
    }
}