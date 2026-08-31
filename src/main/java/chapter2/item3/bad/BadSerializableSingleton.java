package chapter2.item3.bad;

import java.io.Serializable;

public final class BadSerializableSingleton implements Serializable {
    private static final BadSerializableSingleton INSTANCE =
            new BadSerializableSingleton();

    private BadSerializableSingleton() { }

    public static BadSerializableSingleton getInstance() {
        return INSTANCE;
    }
}