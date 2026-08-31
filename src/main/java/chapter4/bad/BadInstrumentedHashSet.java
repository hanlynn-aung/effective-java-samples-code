package chapter4.bad;

import java.util.Collection;
import java.util.HashSet;

public class BadInstrumentedHashSet<E> extends HashSet<E> {
    private int addCount;

    public BadInstrumentedHashSet() {
    }

    public BadInstrumentedHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}