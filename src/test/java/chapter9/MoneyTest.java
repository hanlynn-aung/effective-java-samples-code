package chapter9;

import chapter9.bad.BadDollarsDouble;
import chapter9.good.GoodDollarsBigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MoneyTest {

    @Test
    @DisplayName("Bad: adding 0.10 + 0.20 + 0.30 in double drifts off the exact 0.60")
    void badDoubleDrifts() {
        BadDollarsDouble bad = new BadDollarsDouble(0.10);
        bad.add(0.20);
        bad.add(0.30);
        // The exact sum of these binary-doubles is 0.6000000000000001, not 0.6.
        assertNotEquals(0.6, bad.balance());
        assertEquals(0.6000000000000001, bad.balance());
    }

    @Test
    @DisplayName("Good: BigDecimal with String amounts is exact to the cent")
    void goodBigDecimalIsExact() {
        GoodDollarsBigDecimal good = new GoodDollarsBigDecimal("0.10");
        good.add("0.20");
        good.add("0.30");
        assertEquals(new BigDecimal("0.60"), good.balance());
        assertEquals(0, good.balance().compareTo(new BigDecimal("0.60")));
    }
}