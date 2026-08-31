package chapter5.good;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class GoodSetHelpers {

    public static <E> Set<E> union(Set<E> first, Set<E> second) {
        Set<E> result = new HashSet<>(first);
        result.addAll(second);
        return result;
    }

    public static <E extends Comparable<? super E>> E max(Collection<? extends E> values) {
        E best = null;
        for (E candidate : values) {
            if (best == null || candidate.compareTo(best) > 0) {
                best = candidate;
            }
        }
        return best;
    }
}