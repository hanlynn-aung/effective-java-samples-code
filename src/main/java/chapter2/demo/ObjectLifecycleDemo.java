package chapter2.demo;

import chapter2.item3.bad.BadSerializableSingleton;
import chapter2.item3.bad.BadSingleton;
import chapter2.item3.good.GoodSingleton;
import chapter2.item4.bad.BadUtilityClass;
import chapter2.item4.good.GoodUtilityClass;
import chapter2.item5.bad.BadHardwiredReportService;
import chapter2.item5.bad.BadServiceLocatorReportService;
import chapter2.item5.good.GoodInjectedReportService;
import chapter2.item6.bad.BadBoxedSum;
import chapter2.item6.bad.BadRegexMatcher;
import chapter2.item6.good.GoodPrimitiveSum;
import chapter2.item6.good.GoodRegexMatcher;
import chapter2.item7.bad.BadStack;
import chapter2.item7.good.GoodStack;
import chapter2.item8.good.GoodAutoCloseableResource;
import chapter2.item9.bad.BadTryFinallyFileReader;
import chapter2.item9.good.GoodTryWithResourcesFileReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class ObjectLifecycleDemo {

    public static void main(String[] args) throws Exception {
        item3Singleton();
        item4Noninstantiable();
        item5DependencyInjection();
        item6UnnecessaryObjects();
        item7ObsoleteReferences();
        item8Finalizers();
        item9TryWithResources();
    }

    private static void item3Singleton() throws Exception {
        System.out.println("== Item 3: singleton ==");
        Constructor<BadSingleton> ctor = BadSingleton.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        BadSingleton second = ctor.newInstance();
        System.out.println("reflection created a SECOND BadSingleton: "
                + (second != BadSingleton.getInstance()));

        Constructor<?> enumCtor = GoodSingleton.class.getDeclaredConstructors()[0];
        enumCtor.setAccessible(true);
        try {
            enumCtor.newInstance("INSTANCE", 0);
            System.out.println("enum: reflection SUCCEEDED (unexpected)");
        } catch (IllegalArgumentException e) {
            System.out.println("enum: reflection rejected: " + e.getMessage());
        }

        BadSerializableSingleton copiedBad = (BadSerializableSingleton) copy(
                BadSerializableSingleton.getInstance());
        System.out.println("deserialized field singleton is a NEW instance: "
                + (copiedBad != BadSerializableSingleton.getInstance()));
        GoodSingleton copiedGood = (GoodSingleton) copy(GoodSingleton.INSTANCE);
        System.out.println("deserialized enum singleton keeps its instance: "
                + (copiedGood == GoodSingleton.INSTANCE));
    }

    private static void item4Noninstantiable() throws Exception {
        System.out.println();
        System.out.println("== Item 4: noninstantiability ==");
        System.out.println("bad utility instantiated fine: "
                + new BadUtilityClass().normalize("  X "));
        Constructor<GoodUtilityClass> ctor =
                GoodUtilityClass.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        try {
            ctor.newInstance();
            System.out.println("good utility instantiated (unexpected)");
        } catch (InvocationTargetException e) {
            System.out.println("good utility refuses: " + e.getCause());
        }
    }

    private static void item5DependencyInjection() {
        System.out.println();
        System.out.println("== Item 5: dependency injection ==");
        GoodInjectedReportService.ReportRepository fake =
                () -> "fake (test) report";
        GoodInjectedReportService injected = new GoodInjectedReportService(fake);
        System.out.println("injected service uses the injected impl: "
                + injected.report());

        BadHardwiredReportService hardwired = new BadHardwiredReportService();
        System.out.println("hardwired service is stuck with one impl: "
                + hardwired.report());

        BadServiceLocatorReportService.register(() -> "locator report");
        System.out.println("service-locator service reads global state: "
                + BadServiceLocatorReportService.create().report());
    }

    private static void item6UnnecessaryObjects() {
        System.out.println();
        System.out.println("== Item 6: unnecessary objects ==");
        BadRegexMatcher badRegex = new BadRegexMatcher();
        GoodRegexMatcher goodRegex = new GoodRegexMatcher();
        System.out.println("regex: bad=" + badRegex.isEmail("a@b.co")
                + " good=" + goodRegex.isEmail("a@b.co"));

        int regexRuns = 200_000;
        long t0 = System.nanoTime();
        for (int i = 0; i < regexRuns; i++) {
            badRegex.isEmail("a@b.co");
        }
        long badRegexMs = (System.nanoTime() - t0) / 1_000_000;
        t0 = System.nanoTime();
        for (int i = 0; i < regexRuns; i++) {
            goodRegex.isEmail("a@b.co");
        }
        long goodRegexMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("regex %d runs: bad=%dms good=%dms%n",
                regexRuns, badRegexMs, goodRegexMs);

        int sumRuns = 10_000_000;
        t0 = System.nanoTime();
        new BadBoxedSum().sum(sumRuns);
        long badBoxMs = (System.nanoTime() - t0) / 1_000_000;
        t0 = System.nanoTime();
        new GoodPrimitiveSum().sum(sumRuns);
        long goodBoxMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("boxed sum %d: bad=%dms good=%dms%n",
                sumRuns, badBoxMs, goodBoxMs);
    }

    private static void item7ObsoleteReferences() throws Exception {
        System.out.println();
        System.out.println("== Item 7: obsolete references ==");
        BadStack badStack = new BadStack();
        GoodStack goodStack = new GoodStack();
        badStack.push("a");
        badStack.push("b");
        goodStack.push("a");
        goodStack.push("b");
        badStack.pop();
        goodStack.pop();
        System.out.println("after pop, backing slot[1]: bad="
                + elementAt(badStack, 1) + " good=" + elementAt(goodStack, 1));
    }

    private static Object elementAt(Object stack, int index) throws Exception {
        Field f = stack.getClass().getDeclaredField("elements");
        f.setAccessible(true);
        Object[] elements = (Object[]) f.get(stack);
        return elements[index];
    }

    private static void item8Finalizers() {
        System.out.println();
        System.out.println("== Item 8: finalizers vs AutoCloseable ==");
        try (GoodAutoCloseableResource resource = new GoodAutoCloseableResource()) {
            resource.use();
        }
        GoodAutoCloseableResource closed = new GoodAutoCloseableResource();
        closed.close();
        try {
            closed.use();
            System.out.println("use-after-close allowed (unexpected)");
        } catch (IllegalStateException e) {
            System.out.println("use-after-close rejected: " + e.getMessage());
        }
    }

    private static void item9TryWithResources() {
        System.out.println();
        System.out.println("== Item 9: try-with-resources ==");
        IOException bad = capture(() ->
                new BadTryFinallyFileReader().firstLine(explode()));
        System.out.println("bad (try/finally): surfaces '" + bad.getMessage()
                + "', read boom is lost");
        IOException good = capture(() ->
                new GoodTryWithResourcesFileReader().firstLine(explode()));
        System.out.println("good (try-with-resources): surfaces '"
                + good.getMessage() + "' with suppressed:"
                + suppressedMessages(good));
    }

    private static String suppressedMessages(IOException e) {
        StringBuilder sb = new StringBuilder();
        for (Throwable s : e.getSuppressed()) {
            sb.append(" '").append(s.getMessage()).append("'");
        }
        return sb.length() == 0 ? " none" : sb.toString();
    }

    private static IOException capture(ThrowingAction action) {
        try {
            action.run();
            return new IOException("nothing thrown (unexpected)");
        } catch (IOException e) {
            return e;
        }
    }

    private interface ThrowingAction {
        void run() throws IOException;
    }

    private static Reader explode() {
        return new Reader() {
            private boolean closed;

            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("read boom");
            }

            @Override
            public void close() throws IOException {
                if (closed) {
                    return;
                }
                closed = true;
                throw new IOException("close boom");
            }
        };
    }

    private static Object copy(Object value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            return in.readObject();
        }
    }
}