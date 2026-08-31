package chapter6.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadOrdinalGardener {

    public enum LifeCycle {
        ANNUAL, PERENNIAL, BIENNIAL
    }

    public static final class Plant {
        private final String name;
        private final LifeCycle lifeCycle;

        public Plant(String name, LifeCycle lifeCycle) {
            this.name = name;
            this.lifeCycle = lifeCycle;
        }

        public String name() {
            return name;
        }

        public LifeCycle lifeCycle() {
            return lifeCycle;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Plant>[] classify(List<Plant> plants) {
        int size = LifeCycle.values().length;
        List<Plant>[] buckets = new List[size];
        for (int i = 0; i < size; i++) {
            buckets[i] = new ArrayList<>();
        }
        for (Plant p : plants) {
            buckets[p.lifeCycle().ordinal()].add(p);
        }
        return buckets;
    }
}