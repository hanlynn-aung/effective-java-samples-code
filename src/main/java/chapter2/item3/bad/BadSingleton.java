package chapter2.item3.bad;

public final class BadSingleton {
    private static final BadSingleton INSTANCE = new BadSingleton();

    private BadSingleton() { }

    public static BadSingleton getInstance() {
        return INSTANCE;
    }
}
