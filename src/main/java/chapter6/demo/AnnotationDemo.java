package chapter6.demo;

import chapter6.bad.BadNamingCarrier;
import chapter6.bad.BadNamingRunner;
import chapter6.bad.BadShapeBase;
import chapter6.bad.BadShapeName;
import chapter6.bad.BadAnnotatedOnlyPersistence;
import chapter6.bad.BadPersistable;
import chapter6.good.GoodAnnotatedCarrier;
import chapter6.good.GoodAnnotationRunner;
import chapter6.good.GoodPersistable;
import chapter6.good.GoodRepository;
import chapter6.good.GoodShape;
import chapter6.good.GoodShapeBase;

public class AnnotationDemo {

    public static void main(String[] args) throws Exception {
        item39Annotations();
        item40Override();
        item41MarkerInterfaces();
    }

    private static void item39Annotations() throws Exception {
        System.out.println("== Item 39: annotations over naming patterns ==");

        int badPassed = new BadNamingRunner().run(new BadNamingCarrier());
        System.out.println("bad naming runner: ran " + badPassed
                + " tests, but the typo'd 'tetsMultiply' was never picked up (it also throws!)");

        GoodAnnotationRunner.Result result =
                new GoodAnnotationRunner().run(new GoodAnnotatedCarrier());
        System.out.println("good annotation runner: " + result.passed() + " passed, "
                + result.failed() + " failed");
        System.out.println("  " + result.failures());
    }

    private static void item40Override() {
        System.out.println();
        System.out.println("== Item 40: consistently use @Override ==");

        BadShapeName.Square badSquare = new BadShapeName.Square();
        System.out.println("bad: square.getName()='" + badSquare.getName()
                + "' but through the base type name()='" + ((BadShapeBase) badSquare).name()
                + "'  (the 'override' never fired)");

        GoodShape.Square goodSquare = new GoodShape.Square();
        System.out.println("good: square.name()='" + goodSquare.name()
                + "' and '" + ((GoodShapeBase) goodSquare).name() + "' (override works)");
    }

    private static void item41MarkerInterfaces() {
        System.out.println();
        System.out.println("== Item 41: marker interfaces define types ==");

        GoodRepository repo = new GoodRepository();
        GoodPersistable entity = new GoodPersistable() { };
        repo.save(entity);
        System.out.println("good marker interface: save takes GoodPersistable - a real type,");
        System.out.println("  non-persistable objects fail at COMPILE time, saved=" + repo.hasSaved(entity));

        BadAnnotatedOnlyPersistence annotated = new BadAnnotatedOnlyPersistence();
        @BadPersistable
        final class LocalAnnotated { }
        annotated.save(new LocalAnnotated());
        System.out.println("bad annotation marker: checked at RUNTIME per call, stored=" + annotated.size());
    }
}