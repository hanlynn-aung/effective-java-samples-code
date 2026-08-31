package chapter4;

import chapter4.bad.BadExposedLedger;
import chapter4.good.GoodCapsuleLedger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncapsulationTest {

    @Test
    @DisplayName("Bad: anyone can zero out or sabotage the exposed map")
    void badPublicFieldsAreFullyMutable() {
        BadExposedLedger ledger = new BadExposedLedger();
        ledger.record("a", 100L);
        assertEquals(100L, ledger.entries.get("a"));

        ledger.entries.clear();
        assertTrue(ledger.entries.isEmpty());
    }

    @Test
    @DisplayName("Good: internal state is hidden behind validated methods")
    void goodStateIsHiddenBehindValidatedMethods() {
        GoodCapsuleLedger ledger = GoodCapsuleLedger.create();
        ledger.record("a", 100L);
        ledger.record("a", 50L);
        assertEquals(150L, ledger.balanceOf("a"));
        assertEquals(150L, ledger.total());
        assertThrows(IllegalArgumentException.class, () -> ledger.record("a", -1L));
    }
}