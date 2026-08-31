package chapter6;

import chapter6.bad.BadTextStyle;
import chapter6.good.GoodStyle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumSetTest {

    @Test
    @DisplayName("Bad: bit-field ints accept undefined bits silently")
    void badBitFieldAcceptsGarbage() {
        BadTextStyle style = new BadTextStyle();
        assertEquals("text+bold+italic", style.apply(
                BadTextStyle.STYLE_BOLD | BadTextStyle.STYLE_ITALIC));
        // A caller passes an undefined bit (128) that is not a style at all;
        // the method has no way to reject it, so it is silently ignored.
        assertEquals(style.apply(BadTextStyle.STYLE_BOLD),
                style.apply(BadTextStyle.STYLE_BOLD | 128));
    }

    @Test
    @DisplayName("Bad: bit-field ints have no self-audit for overlapping/empty masks")
    void badBitFieldIsOpaque() {
        BadTextStyle style = new BadTextStyle();
        assertEquals("text", style.apply(0));
        assertEquals("text", style.apply(0x80000000));
    }

    @Test
    @DisplayName("Good: EnumSet of a typed enum can only represent real styles")
    void goodEnumSetIsTypeSafe() {
        GoodStyle style = new GoodStyle();
        assertEquals("text+bold+italic", style.apply(
                EnumSet.of(GoodStyle.Style.BOLD, GoodStyle.Style.ITALIC)));
        assertEquals("text+bold+underline", style.apply(
                EnumSet.of(GoodStyle.Style.UNDERLINE, GoodStyle.Style.BOLD)));
    }
}