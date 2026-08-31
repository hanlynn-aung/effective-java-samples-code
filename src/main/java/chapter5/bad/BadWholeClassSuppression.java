package chapter5.bad;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class BadWholeClassSuppression {
    private final List values = new ArrayList();

    public void add(Object value) {
        values.add(value);
    }

    public List<Integer> asIntegers() {
        return (List<Integer>) (List<?>) values;
    }
}