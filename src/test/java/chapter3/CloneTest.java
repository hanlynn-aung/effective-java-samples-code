package chapter3;

import chapter3.bad.BadShallowClonePerson;
import chapter3.good.GoodCopyFactoryPerson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class CloneTest {

    @Test
    @DisplayName("Bad: shallow clone shares the mutable internal list")
    void badShallowCloneSharesInternals() {
        BadShallowClonePerson original =
                new BadShallowClonePerson("Han", List.of("111", "222"));
        BadShallowClonePerson clone = original.clone();
        clone.phones().add("333");
        assertEquals(List.of("111", "222", "333"), original.phones());
    }

    @Test
    @DisplayName("Good: copy factory produces an independent object")
    void goodCopyFactoryIsIndependent() {
        GoodCopyFactoryPerson original =
                new GoodCopyFactoryPerson("Han", List.of("111", "222"));
        GoodCopyFactoryPerson copy = GoodCopyFactoryPerson.copyOf(original);
        copy.phones().add("333");
        assertEquals(List.of("111", "222"), original.phones());
        assertEquals(List.of("111", "222"), copy.phones());
    }

    @Test
    @DisplayName("Good: copy factory retains the original data")
    void goodCopyFactoryRetainsData() {
        GoodCopyFactoryPerson original =
                new GoodCopyFactoryPerson("Han", List.of("111", "222"));
        GoodCopyFactoryPerson copy = GoodCopyFactoryPerson.copyOf(original);
        assertNotSame(copy, original);
        assertEquals(original.name(), copy.name());
        assertEquals(original.phones(), copy.phones());
    }
}