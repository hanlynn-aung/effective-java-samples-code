package chapter6;

import chapter6.bad.BadFrozenOperation;
import chapter6.good.GoodBasicOperation;
import chapter6.good.GoodExtendedOperation;
import chapter6.good.GoodOperation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtensibleEnumTest {

    @Test
    @DisplayName("All fundamental operations work")
    void frozenOperationWorks() {
        assertEquals(7.0, BadFrozenOperation.PLUS.apply(4, 3));
        assertEquals(1.0, BadFrozenOperation.MINUS.apply(4, 3));
    }

    @Test
    @DisplayName("Bad: one enum cannot be extended - adding an op means editing shared code")
    void frozenEnumCannotAddWithoutEditing() {
        // There is no way to add, say, EXP without changing BadFrozenOperation
        // itself. Consumers of the enum are compiled against a closed type.
        assertEquals(5.0, BadFrozenOperation.TIMES.apply(2.5, 2));
    }

    @Test
    @DisplayName("Good: interface-typed operate() accepts BOTH basic and extended enums")
    void interfaceReceiverAcceptsBothEnumFamilies() {
        assertEquals(5.0, compute(GoodBasicOperation.PLUS, 2, 3));
        assertEquals(8.0, compute(GoodExtendedOperation.EXP, 2, 3));
        assertEquals(2.0, compute(GoodExtendedOperation.REMAINDER, 7, 5));
    }

    private double compute(GoodOperation op, double x, double y) {
        return op.apply(x, y);
    }

    @Test
    @DisplayName("Good: the two families are interchangeable through the shared interface")
    void valuesOfBothFamiliesImplementSameInterface() {
        for (GoodBasicOperation op : GoodBasicOperation.values()) {
            assertEquals(true, op instanceof GoodOperation);
        }
        for (GoodExtendedOperation op : GoodExtendedOperation.values()) {
            assertEquals(true, op instanceof GoodOperation);
        }
        assertEquals(8.0, compute(GoodBasicOperation.PLUS, 5, 3));
        assertEquals(32.0, compute(GoodExtendedOperation.EXP, 2, 5));
    }
}