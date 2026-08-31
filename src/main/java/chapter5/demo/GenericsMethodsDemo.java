package chapter5.demo;

import chapter5.bad.BadHeapPollution;
import chapter5.bad.BadRigidChooser;
import chapter5.bad.BadSetHelpers;
import chapter5.bad.BadStringKeyedFavorites;
import chapter5.good.GoodChooser;
import chapter5.good.GoodCopy;
import chapter5.good.GoodFavorites;
import chapter5.good.GoodSafeVarargs;
import chapter5.good.GoodSetHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GenericsMethodsDemo {

    public static void main(String[] args) {
        item30GenericMethods();
        item31BoundedWildcards();
        item32Varargs();
        item33HeterogeneousContainers();
    }

    private static void item30GenericMethods() {
        System.out.println("== Item 30: favor generic methods ==");

        BadSetHelpers bad = new BadSetHelpers();
        @SuppressWarnings("unchecked")
        Set<String> polluted =
                (Set<String>) bad.union(Set.of("apple"), Set.of(42));
        System.out.println("bad raw union: size=" + polluted.size()
                + " (contains a String AND an Integer, hashSet order is a coin flip)");

        Set<String> merged = GoodSetHelpers.union(Set.of("a", "b"), Set.of("b", "c"));
        System.out.println("good generic union: " + merged);
        System.out.println("good generic max: " + GoodSetHelpers.max(Set.of(3, 7, 42)));
    }

    private static void item31BoundedWildcards() {
        System.out.println();
        System.out.println("== Item 31: bounded wildcards (PECS) ==");

        List<Integer> source = List.of(1, 2, 3);
        List<Number> target = new ArrayList<>();
        GoodCopy.copy(source, target);
        System.out.println("good PECS copy: List<Integer> -> List<Number> -> " + target);

        GoodChooser<Number> chooser = new GoodChooser<>(List.of(1, 2, 3));
        System.out.println("good chooser: GoodChooser<Number> built from List<Integer>, chose="
                + chooser.choose());
        System.out.println("(bad chooser: BadRigidChooser<Number>(List.of(1,2,3)) does not compile)");
    }

    private static void item32Varargs() {
        System.out.println();
        System.out.println("== Item 32: generics + varargs, judiciously ==");

        try {
            BadHeapPollution.dangerous(List.of("a"));
        } catch (ClassCastException e) {
            System.out.println("bad heap pollution: Integer slid into a List<String> vararg array -> CCE");
        }

        List<String> flat = GoodSafeVarargs.flatten(List.of("a", "b"), List.of("c"));
        System.out.println("good safe varargs: flatten never leaks the array -> " + flat);
    }

    private static void item33HeterogeneousContainers() {
        System.out.println();
        System.out.println("== Item 33: typesafe heterogeneous containers ==");

        BadStringKeyedFavorites strings = new BadStringKeyedFavorites();
        strings.put("favorite number", 42);
        try {
            String wrecked = (String) strings.get("favorite number");
        } catch (ClassCastException e) {
            System.out.println("bad string-keyed favorites: caller's cast on an Integer -> CCE");
        }

        GoodFavorites favorites = new GoodFavorites();
        favorites.put(String.class, "java");
        favorites.put(Integer.class, 42);
        favorites.put(Class.class, GoodFavorites.class);
        System.out.println("good favorites: string=" + favorites.get(String.class)
                + " int=" + favorites.get(Integer.class)
                + " class=" + favorites.get(Class.class).getSimpleName());
    }
}