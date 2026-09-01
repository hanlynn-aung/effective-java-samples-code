package chapter11.good;

/**
 * Lazy-initialised static field using the holder-class idiom - the preferred
 * correct pattern: the JVM synchronises class initialisation, so no explicit
 * lock or volatile is needed.
 */
public final class GoodLazyHolder {

    private static final class InstanceHolder {
        static final GoodLazyHolder INSTANCE = new GoodLazyHolder();
    }

    public static GoodLazyHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private final String name = "singleton";

    public String name() {
        return name;
    }
}