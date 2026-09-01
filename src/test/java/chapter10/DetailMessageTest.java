package chapter10;

import chapter10.bad.BadVagueMessage;
import chapter10.good.GoodDetailMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DetailMessageTest {

    @Test
    @DisplayName("Bad: a vague 'invalid'/'bad index' message gives nothing to act on")
    void badVagueMessage() {
        BadVagueMessage bad = new BadVagueMessage();
        IllegalArgumentException div = assertThrows(
                IllegalArgumentException.class, () -> bad.divide(10, 0));
        assertTrue(div.getMessage().equals("invalid") || div.getMessage() == null);
    }

    @Test
    @DisplayName("Good: the detail message captures the values needed to reproduce the failure")
    void goodCapturesValues() {
        GoodDetailMessage good = new GoodDetailMessage();
        IllegalArgumentException div = assertThrows(
                IllegalArgumentException.class, () -> good.divide(10, 0));
        assertTrue(div.getMessage().contains("10"));
        assertTrue(div.getMessage().contains("0"));

        IndexOutOfBoundsException e = assertThrows(
                IndexOutOfBoundsException.class, () -> good.at("abc", 9));
        assertTrue(e.getMessage().contains("9"));
        assertTrue(e.getMessage().contains("3"));
    }
}