package chapter5.demo;

import chapter5.bad.BadCovariantArray;
import chapter5.bad.BadNumbers;
import chapter5.bad.BadObjectStack;
import chapter5.bad.BadWholeClassSuppression;
import chapter5.good.GoodGenericStack;
import chapter5.good.GoodNumberList;
import chapter5.good.GoodNumbers;
import chapter5.good.GoodScopedSuppression;

import java.util.Arrays;
import java.util.List;

public class GenericsTypesDemo {

    public static void main(String[] args) {
        item26RawTypes();
        item27UncheckedWarnings();
        item28ListsVsArrays();
        item29GenericTypes();
    }

    private static void item26RawTypes() {
        System.out.println("== Item 26: don't use raw types ==");

        BadNumbers bad = new BadNumbers();
        List values = bad.values();
        System.out.println("bad raw list: size=" + values.size() + ", contains "
                + Arrays.toString(values.toArray()));

        GoodNumbers good = new GoodNumbers();
        List<Integer> numbers = good.values();
        System.out.println("good typed list: typed as List<Integer>, summing="
                + numbers.stream().mapToInt(Integer::intValue).sum());
    }

    private static void item27UncheckedWarnings() {
        System.out.println();
        System.out.println("== Item 27: eliminate unchecked warnings ==");

        BadWholeClassSuppression hidden = new BadWholeClassSuppression();
        hidden.add(42);
        hidden.add("not an int");
        try {
            Integer exploded = hidden.asIntegers().get(1);
        } catch (ClassCastException e) {
            System.out.println("bad class-wide suppression: a String was hiding inside a "
                    + "List<Integer> (the cast slid under @SuppressWarnings) -> " + e);
        }

        GoodScopedSuppression helper = new GoodScopedSuppression();
        System.out.println("good redesigned helper: parses strings into a real List<Integer> -> "
                + helper.asIntegers(List.of("1", "2", "3")));
    }

    private static void item28ListsVsArrays() {
        System.out.println();
        System.out.println("== Item 28: prefer lists to arrays ==");

        BadCovariantArray bad = new BadCovariantArray();
        try {
            bad.covariantTrap();
        } catch (ArrayStoreException e) {
            System.out.println("bad covariant array: String into Long[] caught only AT WRITE -> "
                    + e.getClass().getSimpleName());
        }

        GoodNumberList good = new GoodNumberList();
        List<Number> numbers = good.mixedNumbers();
        System.out.println("good generic list: List<Number> holds Integer + Double -> "
                + numbers);
    }

    private static void item29GenericTypes() {
        System.out.println();
        System.out.println("== Item 29: favor generic types ==");

        BadObjectStack backToRaw = new BadObjectStack();
        backToRaw.push("a");
        backToRaw.push(7);
        try {
            String top = (String) backToRaw.pop();
            System.out.println("bad Object stack: popped " + top);
        } catch (ClassCastException e) {
            System.out.println("bad Object stack: the pushed Integer was not a String -> CCE");
        }

        GoodGenericStack<String> stack = new GoodGenericStack<>();
        stack.push("a");
        stack.push("b");
        System.out.println("good generic stack: pop=" + stack.pop() + " then " + stack.pop()
                + ", all elements are String by construction");
    }
}