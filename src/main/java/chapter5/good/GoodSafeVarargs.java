package chapter5.good;

import java.util.ArrayList;
import java.util.List;

public final class GoodSafeVarargs {

    @SafeVarargs
    public static <T> List<T> flatten(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }
}