package chapter8;

import chapter8.bad.BadPeriod;
import chapter8.good.GoodPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefensiveCopyTest {

    @Test
    @DisplayName("Bad: mutating the getter's Date corrupts an 'immutable' period")
    void badGetterLeaksMutableState() {
        Date start = new Date(0);
        Date end = new Date(1000);
        BadPeriod period = new BadPeriod(start, end);
        assertTrue(period.lengthMillis() > 0);

        period.getStart().setTime(5000);
        assertTrue(period.lengthMillis() < 0, "caller silently rewrote the period");
    }

    @Test
    @DisplayName("Bad: the original Date reference still aliases the period's internal state")
    void badConstructorAliasesCallerReference() {
        Date start = new Date(0);
        Date end = new Date(1000);
        BadPeriod period = new BadPeriod(start, end);
        start.setTime(10_000);
        assertTrue(period.lengthMillis() < 0, "changing the caller's Date changed the period");
    }

    @Test
    @DisplayName("Good: defensive copies in the constructor and getters keep the period stable")
    void goodCopiesBidirectional() {
        Date start = new Date(0);
        Date end = new Date(1000);
        GoodPeriod period = new GoodPeriod(start, end);
        start.setTime(5000);
        period.getStart().setTime(9000);
        // Neither the caller's Date nor the getter's copy can corrupt the period.
        assertTrue(period.lengthMillis() > 0);
    }
}