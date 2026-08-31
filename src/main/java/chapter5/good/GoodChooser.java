package chapter5.good;

import java.util.Collection;
import java.util.List;

public final class GoodChooser<T> {
    private final List<? extends T> choices;

    public GoodChooser(Collection<? extends T> choices) {
        this.choices = List.copyOf(choices);
    }

    public T choose() {
        return choices.get(0);
    }
}