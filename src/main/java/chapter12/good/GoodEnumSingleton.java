package chapter12.good;

/**
 * An {@code enum} singleton - instance control for free. The language guarantees
 * exactly one instance per enum value, serialization cannot create another, and
 * there is no {@code readResolve} to get wrong.
 */
public enum GoodEnumSingleton {

    INSTANCE;

    private int counter;

    public int tick() {
        return ++counter;
    }
}