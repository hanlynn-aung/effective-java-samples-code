package chapter4;

import chapter4.bad.BadNewspaper;
import chapter4.good.GoodNewspaper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessorTest {

    @Test
    @DisplayName("Bad: naked public fields invite silent tampering")
    void badPublicFieldsInviteTampering() {
        BadNewspaper paper = new BadNewspaper();
        paper.headline = "Real headline";
        paper.headline = "Tampered!";
        assertEquals("Tampered!", paper.headline);
    }

    @Test
    @DisplayName("Good: accessors read private fields")
    void goodAccessorsReadPrivateFields() {
        GoodNewspaper paper =
                new GoodNewspaper("Peace", "Hanlynn Aung", List.of("a", "b"));
        assertEquals("Peace", paper.headline());
        assertEquals("Hanlynn Aung", paper.writer());
        assertEquals(List.of("a", "b"), paper.articles());
    }

    @Test
    @DisplayName("Good: accessors return defensive copies of mutable state")
    void goodAccessorReturnsDefensiveCopy() {
        GoodNewspaper paper =
                new GoodNewspaper("Peace", "Hanlynn Aung", List.of("a", "b"));
        List<String> view = paper.articles();
        view.add("c");
        view.clear();
        assertEquals(List.of("a", "b"), paper.articles());
    }
}