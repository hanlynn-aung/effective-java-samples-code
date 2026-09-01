package chapter12.bad;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;

/**
 * (bad) A hand-rolled singleton that relies on {@code readResolve} to preserve
 * instance control across deserialization. This is fragile: it must be exactly
 * right, it must agree about equality, and it silently breaks data integrity if
 * any field affects the instance (e.g. the {@code state} here) - the resolved
 * singleton {@code readResolve} returns is the *original*, so serialized state
 * is discarded. An enum gives all of this for free.
 */
public final class BadReadResolveSingleton implements Serializable {

    @Serial
    private static final long serialVersionUID = 123L;

    private static final BadReadResolveSingleton INSTANCE = new BadReadResolveSingleton();

    private String state = "initial";

    private BadReadResolveSingleton() {
    }

    public static BadReadResolveSingleton getInstance() {
        return INSTANCE;
    }

    public void setState(String value) {
        this.state = value;
    }

    public String state() {
        return state;
    }

    @Serial
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }
}