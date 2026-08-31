package chapter8.good;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class GoodDispatch {

    public String classify(Set<?> values) {
        return "set";
    }

    public String classifyAsList(List<?> values) {
        return "list";
    }

    public String classify(Collection<?> values) {
        return "collection";
    }
}