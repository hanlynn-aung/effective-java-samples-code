package chapter6;

import chapter6.bad.BadMeritLevels;
import chapter6.good.GoodMeritLevels;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrdinalTest {

    @Test
    @DisplayName("Bad: ordinal()-derived rank silently breaks when constants are reordered")
    void badOrdinalRankBreaksOnReordering() {
        // Declared LOW, HIGH, MEDIUM -> ordinals 0,1,2. The intended tiers are
        // LOW=1, MEDIUM=2, HIGH=3, but ordinal+1 reads 1,2,3 left-to-right.
        assertEquals(1, BadMeritLevels.BadMerit.LOW.rank());
        assertEquals(2, BadMeritLevels.BadMerit.HIGH.rank());
        assertEquals(3, BadMeritLevels.BadMerit.MEDIUM.rank());
        // MEDIUM got tier 3 when it should be 2 -- silently wrong, no warning anywhere.
    }

    @Test
    @DisplayName("Good: a stored field keeps the intended rank regardless of declaration order")
    void goodFieldRankIgnoresDeclarationOrder() {
        assertEquals(1, GoodMeritLevels.GoodMerit.LOW.tier());
        assertEquals(3, GoodMeritLevels.GoodMerit.HIGH.tier());
        assertEquals(2, GoodMeritLevels.GoodMerit.MEDIUM.tier());
    }
}