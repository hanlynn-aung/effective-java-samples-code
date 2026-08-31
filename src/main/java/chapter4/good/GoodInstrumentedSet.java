package chapter4.good;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class GoodInstrumentedSet<E> {
    private final Set<E> delegate;
    private int addCount;

    private GoodInstrumentedSet(Set<E> delegate) {
        this.delegate = delegate;
    }

    public static <E> GoodInstrumentedSet<E> of() {
        return new GoodInstrumentedSet<>(new HashSet<>());
    }

    public boolean add(E e) {
        boolean changed = delegate.add(e);
        if (changed) {
            addCount++;
        }
        return changed;
    }

    public boolean addAll(Collection<? extends E> c) {
        int before = delegate.size();
        boolean changed = delegate.addAll(c);
        addCount += delegate.size() - before;
        return changed;
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public int size() {
        return delegate.size();
    }

    public int getAddCount() {
        return addCount;
    }
}