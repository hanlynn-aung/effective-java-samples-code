package chapter3.demo;

import chapter3.bad.BadCaseInsensitiveString;
import chapter3.bad.BadHashPoint;
import chapter3.bad.BadInconsistentPerson;
import chapter3.bad.BadPoint;
import chapter3.bad.BadShallowClonePerson;
import chapter3.bad.BadSubtractionComparator;
import chapter3.bad.BadTransitivityColorPoint;
import chapter3.bad.BadTransitivityPoint;
import chapter3.good.GoodComparablePerson;
import chapter3.good.GoodCopyFactoryPerson;
import chapter3.good.GoodPoint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class ObjectContractsDemo {

    public static void main(String[] args) {
        item10Equals();
        item11HashCode();
        item12ToString();
        item13Clone();
        item14Comparable();
    }

    private static BadPoint point(int x, int y) {
        BadPoint point = new BadPoint();
        point.x = x;
        point.y = y;
        return point;
    }

    private static void item10Equals() {
        System.out.println("== Item 10: equals ==");
        System.out.println("bad identity equals, same coordinates: "
                + point(1, 2).equals(point(1, 2)));
        System.out.println("good value equals, same coordinates: "
                + new GoodPoint(1, 2).equals(new GoodPoint(1, 2)));

        BadCaseInsensitiveString cis = new BadCaseInsensitiveString("Foo");
        System.out.println("symmetry: cis.equals(\"foo\")=" + cis.equals("foo")
                + " but \"foo\".equals(cis)=" + "foo".equals(cis));

        BadTransitivityPoint p = new BadTransitivityPoint(1, 2);
        BadTransitivityColorPoint red =
                new BadTransitivityColorPoint(1, 2, "red");
        BadTransitivityColorPoint blue =
                new BadTransitivityColorPoint(1, 2, "blue");
        System.out.println("transitivity: p.equals(red)=" + p.equals(red)
                + " p.equals(blue)=" + p.equals(blue)
                + " red.equals(blue)=" + red.equals(blue));
    }

    private static void item11HashCode() {
        System.out.println();
        System.out.println("== Item 11: hashCode ==");
        BadHashPoint a = new BadHashPoint(1, 2);
        BadHashPoint b = new BadHashPoint(1, 2);
        System.out.println("bad: equals=" + a.equals(b)
                + " hashCode a=" + a.hashCode() + " b=" + b.hashCode());

        Set<Object> badSet = new HashSet<>();
        badSet.add(a);
        badSet.add(b);
        System.out.println("bad HashSet with two EQUAL elements: size="
                + badSet.size());

        Set<GoodPoint> goodSet = new HashSet<>();
        goodSet.add(new GoodPoint(1, 2));
        goodSet.add(new GoodPoint(1, 2));
        System.out.println("good HashSet dedups: size=" + goodSet.size());
    }

    private static void item12ToString() {
        System.out.println();
        System.out.println("== Item 12: toString ==");
        System.out.println("bad default: " + point(1, 2));
        System.out.println("good custom: " + new GoodPoint(1, 2));
    }

    private static void item13Clone() {
        System.out.println();
        System.out.println("== Item 13: clone vs copy factory ==");
        BadShallowClonePerson original =
                new BadShallowClonePerson("Han", List.of("111", "222"));
        BadShallowClonePerson shallow = original.clone();
        shallow.phones().add("333");
        System.out.println("bad shallow clone: adding to clone's phones, "
                + "original now sees " + original.phones());

        GoodCopyFactoryPerson goodOriginal =
                new GoodCopyFactoryPerson("Han", List.of("111", "222"));
        GoodCopyFactoryPerson copy = GoodCopyFactoryPerson.copyOf(goodOriginal);
        copy.phones().add("333");
        System.out.println("good copy factory: adding to copy's phones, "
                + "original still sees " + goodOriginal.phones());
    }

    private static void item14Comparable() {
        System.out.println();
        System.out.println("== Item 14: Comparable ==");
        System.out.println("bad subtraction comparator: "
                + "compare(MAX_VALUE, -1)="
                + new BadSubtractionComparator()
                        .compare(new BadSubtractionComparator.Item(Integer.MAX_VALUE),
                                new BadSubtractionComparator.Item(-1))
                + "  (MAX < -1)");

        TreeSet<BadInconsistentPerson> badSet = new TreeSet<>();
        badSet.add(new BadInconsistentPerson("Han", 10));
        badSet.add(new BadInconsistentPerson("Han", 20));
        System.out.println("bad compareTo/equals mismatch: TreeSet size="
                + badSet.size() + " though the two persons are not equal");

        TreeSet<GoodComparablePerson> goodSet = new TreeSet<>();
        goodSet.add(new GoodComparablePerson("Han", 10));
        goodSet.add(new GoodComparablePerson("Han", 20));
        System.out.println("good consistent compareTo: TreeSet size="
                + goodSet.size());
    }
}