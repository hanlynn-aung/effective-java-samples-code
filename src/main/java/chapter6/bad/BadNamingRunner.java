package chapter6.bad;

import java.lang.reflect.Method;

public final class BadNamingRunner {

    public int run(Object testee) throws Exception {
        int passed = 0;
        for (Method method : testee.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("test")) {
                method.invoke(testee);
                passed++;
            }
        }
        return passed;
    }
}