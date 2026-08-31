package chapter5.bad;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class BadSetHelpers {

    public Set union(Set a, Set b) {
        Set result = new HashSet(a);
        result.addAll(b);
        return result;
    }

    public Comparable max(Set values) {
        Comparable best = null;
        for (Object value : values) {
            Comparable candidate = (Comparable) value;
            if (best == null || candidate.compareTo(best) > 0) {
                best = candidate;
            }
        }
        return best;
    }
}