package chapter5.bad;

import java.util.List;

public final class BadRigidCopy {

    public static <E> void copy(List<E> source, List<E> target) {
        for (E element : source) {
            target.add(element);
        }
    }
}