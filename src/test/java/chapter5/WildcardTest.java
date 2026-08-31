package chapter5;

import chapter5.bad.BadRigidChooser;
import chapter5.bad.BadRigidCopy;
import chapter5.good.GoodChooser;
import chapter5.good.GoodCopy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WildcardTest {

    @Test
    @DisplayName("Bad: a rigid copy needs identical types on both sides")
    void badRigidCopyDemandsEqualTypes() {
        List<Integer> numbers = List.of(1, 2, 3);
        List<Integer> target = new ArrayList<>();
        BadRigidCopy.copy(numbers, target);
        assertEquals(List.of(1, 2, 3), target);
        // List<Integer> -> List<Number> does not compile: no wildcards.
        // (compile-refusal, not a runtime failure, is the point of the Bad sample)
    }

    @Test
    @DisplayName("Good: producer-extends lets List<Integer> feed a List<Number> target")
    void goodCopyAcceptsWiderTarget() {
        List<Integer> source = List.of(1, 2, 3);
        List<Number> target = new ArrayList<>();
        GoodCopy.copy(source, target);
        assertEquals(List.of(1, 2, 3), target);
    }

    @Test
    @DisplayName("Good: PECS consumer-super lets a Number source fill an Object target")
    void goodCopyAcceptsWiderTargetWorksTheOtherWay() {
        List<Number> source = new ArrayList<>();
        source.add(1);
        source.add(2.5);
        List<Object> target = new ArrayList<>();
        GoodCopy.copy(source, target);
        assertEquals(List.of(1, 2.5), target);
    }

    @Test
    @DisplayName("Bad: rigid chooser can only accept exactly its own type")
    void badRigidChooserRequiresExactTypeProducers() {
        BadRigidChooser<Integer> chooser = new BadRigidChooser<>(List.of(10, 20));
        assertEquals(10, chooser.choose());
        // new BadRigidChooser<Number>(List.of(1, 2, 3)) does not compile; a
        // producer of Integers cannot be handed to a Chooser<Number>.
    }

    @Test
    @DisplayName("Good: a producer-extends chooser accepts narrower types")
    void goodChooserAcceptsProducerOfNarrowerType() {
        GoodChooser<Number> chooser = new GoodChooser<>(List.of(1, 2, 3));
        Number chosen = chooser.choose();
        assertEquals(1, chosen);
    }
}