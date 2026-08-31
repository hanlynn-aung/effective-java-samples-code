package chapter5.good;

import java.util.ArrayList;
import java.util.List;

public final class GoodScopedSuppression {

    public List<Integer> asIntegers(List<String> values) {
        List<Integer> integers = new ArrayList<>(values.size());
        for (String value : values) {
            integers.add(Integer.valueOf(value));
        }
        return integers;
    }
}