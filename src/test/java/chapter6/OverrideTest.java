package chapter6;

import chapter6.bad.BadShapeBase;
import chapter6.bad.BadShapeName;
import chapter6.good.GoodShape;
import chapter6.good.GoodShapeBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverrideTest {

    @Test
    @DisplayName("Bad: getName() silently HIDES name() instead of overriding - polymorphism breaks")
    void badHidesInsteadOfOverrides() {
        BadShapeName.Square square = new BadShapeName.Square();
        // Calling through the typed reference hits the new method...
        assertEquals("square", square.getName());
        // ...but through the base type the override never kicked in.
        BadShapeBase asBase = square;
        assertEquals("base", asBase.name());
    }

    @Test
    @DisplayName("Good: @Override names the real method - the override actually fires")
    void goodOverrideFiresPolymorphically() {
        GoodShape.Square square = new GoodShape.Square();
        assertEquals("square", square.name());
        GoodShapeBase asBase = square;
        assertEquals("square", asBase.name());
    }
}