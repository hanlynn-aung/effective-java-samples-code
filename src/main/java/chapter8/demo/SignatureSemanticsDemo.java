package chapter8.demo;

import chapter8.bad.BadNullReturn;
import chapter8.bad.BadOptionalOveruse;
import chapter8.bad.BadVarargs;
import chapter8.good.GoodDispatch;
import chapter8.good.GoodEmptyReturn;
import chapter8.good.GoodOptional;
import chapter8.good.GoodVarargs;

import java.util.ArrayList;
import java.util.List;

public final class SignatureSemanticsDemo {

    public static void main(String[] args) {
        item52Overloading();
        item53Varargs();
        item54EmptyReturns();
        item55Optionals();
        item56DocComments();
    }

    static void item52Overloading() {
        System.out.println("== Item 52: use overloading judiciously ==");
        GoodDispatch good = new GoodDispatch();
        List<String> list = new ArrayList<>();
        System.out.println("good distinct names: classifyAsList(list) = "
                + good.classifyAsList(list) + " (distinct name = no compile-time surprise)");
    }

    static void item53Varargs() {
        System.out.println();
        System.out.println("== Item 53: use varargs judiciously ==");
        try {
            new BadVarargs().min();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("bad varargs: min() with zero args -> " + e);
        }
        System.out.println("good varargs: min(2,5,3) = " + new GoodVarargs().min(2, 5, 3)
                + " (required first arg prevents the empty case)");
    }

    static void item54EmptyReturns() {
        System.out.println();
        System.out.println("== Item 54: return empty collections, not nulls ==");
        try {
            new BadNullReturn().find("").size();
        } catch (NullPointerException e) {
            System.out.println("bad return: find(\"\") gave null -> NPE on .size()");
        }
        List<String> empty = new GoodEmptyReturn().find("");
        System.out.println("good return: find(\"\") size=" + empty.size()
                + " (empty list, safe to iterate)");
    }

    static void item55Optionals() {
        System.out.println();
        System.out.println("== Item 55: return optionals judiciously ==");
        BadOptionalOveruse bad = new BadOptionalOveruse();
        System.out.println("bad: Optional wrapping a List -> " + bad.words().get().size()
                + " words (the List itself should just be returned)");
        GoodOptional good = new GoodOptional();
        System.out.println("good: List returned directly (" + good.words().size()
                + " words), OptionalDouble for price=" + good.lastPrice().getAsDouble()
                + ", absent value -> " + good.middle("").orElse("fallback"));
    }

    static void item56DocComments() {
        System.out.println();
        System.out.println("== Item 56: write doc comments ==");
        System.out.println("bad: BadDocumented.rate has no javadoc, no @throws, no @return");
        System.out.println("good: GoodDocumented.rate documents @param, @return, @throws, @implSpec");
    }
}