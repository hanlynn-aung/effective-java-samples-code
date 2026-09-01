package chapter10.demo;

import chapter10.bad.BadExceptionControlFlow;
import chapter10.bad.BadInventedException;
import chapter10.bad.BadLeakyException;
import chapter10.bad.BadOverChecked;
import chapter10.bad.BadCheckedForBug;
import chapter10.good.GoodCheckedRuntime;
import chapter10.good.GoodConditionCheck;
import chapter10.good.GoodStandardException;
import chapter10.good.GoodTranslation;

import java.util.Arrays;
import java.util.List;

public final class ExceptionDesignDemo {

    public static void main(String[] args) throws Exception {
        item69ControlFlow();
        item70CheckedVsRuntime();
        item71CheckedOveruse();
        item72Standard();
        item73Translation();
    }

    static void item69ControlFlow() {
        System.out.println("== Item 69: use exceptions only for exceptional conditions ==");
        List<String> values = Arrays.asList("a", "b", "c");
        System.out.println("bad (NoSuchElement as control flow): contains 'b' = "
                + new BadExceptionControlFlow().contains(values, "b"));
        System.out.println("good (plain loop): contains 'b' = "
                + new GoodConditionCheck().contains(values, "b")
                + ", digits in 'a1b2' = "
                + new GoodConditionCheck().countDigits("a1b2"));
    }

    static void item70CheckedVsRuntime() {
        System.out.println();
        System.out.println("== Item 70: checked for recoverable, runtime for programming errors ==");
        GoodCheckedRuntime good = new GoodCheckedRuntime();
        try {
            good.withdraw(10, 100);
        } catch (GoodCheckedRuntime.InsufficientFundsException e) {
            System.out.println("recoverable (insufficient funds) -> checked "
                    + e.getClass().getSimpleName() + " the caller must handle: " + e.getMessage());
        }
        try {
            good.divide(1, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("programmer error (bad arg) -> unforced runtime "
                    + e.getClass().getSimpleName());
        }
        try {
            new BadCheckedForBug().setAge(-1);
        } catch (BadCheckedForBug.ProgrammingError e) {
            System.out.println("bad: a bug forced into a checked exception -> " + e);
        }
    }

    static void item71CheckedOveruse() {
        System.out.println();
        System.out.println("== Item 71: avoid unnecessary checked exceptions ==");
        BadOverChecked bad = new BadOverChecked();
        try {
            bad.put("k", "v");
            System.out.println("bad: put/get/size all declare StorageException - "
                    + "callers must catch everywhere (size() can't even fail!)");
        } catch (BadOverChecked.StorageException e) {
            System.out.println("bad: forced catch -> " + e);
        }
    }

    static void item72Standard() {
        System.out.println();
        System.out.println("== Item 72: favor the use of standard exceptions ==");
        try {
            new BadInventedException().requiredIndex(null);
        } catch (BadInventedException.MyException e) {
            System.out.println("bad: custom MyException for a null/empty case -> " + e);
        }
        try {
            new GoodStandardException().requiredIndex("");
        } catch (IllegalArgumentException e) {
            System.out.println("good: standard IllegalArgumentException for empty -> " + e);
        }
    }

    static void item73Translation() {
        System.out.println();
        System.out.println("== Item 73: throw exceptions appropriate to the abstraction ==");
        try {
            new BadLeakyException().loadConfig("x");
        } catch (RuntimeException e) {
            System.out.println("bad: raw SQLException leaks from loadConfig (cause="
                    + e.getCause().getClass().getSimpleName() + ")");
        }
        try {
            new GoodTranslation().loadConfig("x");
        } catch (GoodTranslation.ConfigLoaderException e) {
            System.out.println("good: translated to ConfigLoaderException (cause="
                    + e.getCause().getClass().getSimpleName() + ", msg='" + e.getMessage() + "')");
        }
    }
}