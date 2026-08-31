package chapter8;

import chapter8.good.GoodDocumented;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocCommentTest {

    @Test
    @DisplayName("Good: the documented behaviour - throws on NaN/negative - is actually honoured")
    void documentedContractIsHonoured() {
        GoodDocumented doc = new GoodDocumented();
        assertThrows(IllegalArgumentException.class, () -> doc.rate(-1));
        assertThrows(IllegalArgumentException.class, () -> doc.rate(Double.NaN));
        assertEquals(0.8, doc.rate(10.0), 1e-9);
    }
}