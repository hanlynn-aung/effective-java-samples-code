package chapter8.bad;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class BadReload {

    public String classify(Set<?> values) {
        return "set";
    }

    public String classify(List<?> values) {
        return "list";
    }

    public String classify(Collection<?> values) {
        return "collection";
    }
}