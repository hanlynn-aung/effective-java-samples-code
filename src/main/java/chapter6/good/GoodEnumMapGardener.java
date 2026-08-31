package chapter6.good;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class GoodEnumMapGardener {

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

    public static Map<LifeCycle, List<Plant>> classify(List<Plant> plants) {
        Map<LifeCycle, List<Plant>> groups = new EnumMap<>(LifeCycle.class);
        for (LifeCycle lc : LifeCycle.values()) {
            groups.put(lc, new ArrayList<>());
        }
        for (Plant p : plants) {
            groups.get(p.lifeCycle()).add(p);
        }
        return groups;
    }
}