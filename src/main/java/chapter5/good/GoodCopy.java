package chapter5.good;

import java.util.List;

public final class GoodCopy {

    public static <E> void copy(List<? extends E> source, List<? super E> target) {
        for (E element : source) {
            target.add(element);
        }
    }
}