package chapter12.good;

import java.util.ArrayList;
import java.util.List;

/** Stores names while preventing callers from mutating internal state. */
public final class GoodService {
    private final List<String> names = new ArrayList<>();

    /** Adds a non-null name to the service. */
    public void add(String name) {
        names.add(java.util.Objects.requireNonNull(name, "name"));
    }

    /** Returns a read-only snapshot of the current names. */
    public List<String> names() {
        return List.copyOf(names);
    }
}
