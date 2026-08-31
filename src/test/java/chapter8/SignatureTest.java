package chapter8;

import chapter8.bad.BadSignature;
import chapter8.good.GoodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureTest {

    @Test
    @DisplayName("Bad: three adjacent booleans make the call site a guessing game")
    void badFlagsAreUnreadable() {
        BadSignature bad = new BadSignature();
        // Which order? And the 4th boolean flips the meaning of the previous three.
        boolean result = bad.qualify("alice", "us", true, true, true, false);
        assertEquals(true, result);
    }

    @Test
    @DisplayName("Good: an enum-based vararg reads clearly and stays type-safe")
    void goodEnumIsSelfDocumenting() {
        GoodSignature good = new GoodSignature();
        assertTrue(good.qualifies("alice", "us",
                new GoodSignature.Requirement[]{GoodSignature.Requirement.ACTIVE}));
        assertTrue(good.qualifies("alice", "us",
                new GoodSignature.Requirement[]{GoodSignature.Requirement.ACTIVE,
                        GoodSignature.Requirement.VERIFIED}));
    }

    @Test
    @DisplayName("Good: no booleans means no fail-closed-to-fail-open confusion")
    void goodHasNoSlidingBooleanMeaning() {
        GoodSignature good = new GoodSignature();
        boolean empty = good.qualifies("a", "us", new GoodSignature.Requirement[]{});
        boolean active = good.qualifies("a", "us",
                new GoodSignature.Requirement[]{GoodSignature.Requirement.ACTIVE});
        assertTrue(empty);   // no requirements to violate -> holds
        assertTrue(active);  // requirement enumerated explicitly, no ambiguity
    }
}