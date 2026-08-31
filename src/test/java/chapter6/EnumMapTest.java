package chapter6;

import chapter6.bad.BadOrdinalGardener;
import chapter6.good.GoodEnumMapGardener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumMapTest {

    @Test
    @DisplayName("Bad: ordinal-indexed buckets silently miss life cycles that never appear")
    void badOrdinalBucketsKeepNullHoles() {
        List<BadOrdinalGardener.Plant> plants = List.of(
                new BadOrdinalGardener.Plant("rose", BadOrdinalGardener.LifeCycle.PERENNIAL));

        List<BadOrdinalGardener.Plant>[] buckets =
                BadOrdinalGardener.classify(plants);

        assertTrue(buckets[BadOrdinalGardener.LifeCycle.ANNUAL.ordinal()].isEmpty());
        assertEquals(1, buckets[BadOrdinalGardener.LifeCycle.PERENNIAL.ordinal()].size());
        // One hole is invisible - the annual bucket is just empty. Add one more
        // enum value later and every ordinal shifts, corrupting every bucket.
    }

    @Test
    @DisplayName("Good: EnumMap groups every lifecycle with a real key")
    void goodEnumMapKeepsEveryKeyPresent() {
        List<GoodEnumMapGardener.Plant> plants = List.of(
                new GoodEnumMapGardener.Plant("rose", GoodEnumMapGardener.LifeCycle.PERENNIAL));

        Map<GoodEnumMapGardener.LifeCycle, List<GoodEnumMapGardener.Plant>> groups =
                GoodEnumMapGardener.classify(plants);

        for (GoodEnumMapGardener.LifeCycle lc : GoodEnumMapGardener.LifeCycle.values()) {
            assertTrue(groups.containsKey(lc), "every lifecycle must have a key");
        }
        assertEquals(1, groups.get(GoodEnumMapGardener.LifeCycle.PERENNIAL).size());
        assertEquals(0, groups.get(GoodEnumMapGardener.LifeCycle.ANNUAL).size());
    }
}