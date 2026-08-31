package chapter5.bad;

import java.util.Collection;

public final class BadRigidChooser<T> {
    private final T choice;

    public BadRigidChooser(Collection<T> choices) {
        this.choice = choices.iterator().next();
    }

    public T choose() {
        return choice;
    }
}