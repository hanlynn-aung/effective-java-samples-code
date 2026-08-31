package chapter3.bad;

import java.util.ArrayList;
import java.util.List;

public class BadShallowClonePerson implements Cloneable {
    private final String name;
    private final List<String> phones;

    public BadShallowClonePerson(String name, List<String> phones) {
        this.name = name;
        this.phones = new ArrayList<>(phones);
    }

    public String name() { return name; }
    public List<String> phones() { return phones; }

    @Override
    public BadShallowClonePerson clone() {
        try {
            return (BadShallowClonePerson) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}