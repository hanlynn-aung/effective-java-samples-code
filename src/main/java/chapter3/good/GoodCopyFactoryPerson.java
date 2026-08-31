package chapter3.good;

import java.util.ArrayList;
import java.util.List;

public final class GoodCopyFactoryPerson {
    private final String name;
    private final List<String> phones;

    public GoodCopyFactoryPerson(String name, List<String> phones) {
        this.name = name;
        this.phones = new ArrayList<>(phones);
    }

    public static GoodCopyFactoryPerson copyOf(GoodCopyFactoryPerson other) {
        return new GoodCopyFactoryPerson(other.name, other.phones);
    }

    public String name() { return name; }

    public List<String> phones() {
        return new ArrayList<>(phones);
    }
}