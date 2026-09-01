package chapter9.bad;

import java.lang.reflect.Constructor;

public final class BadReflective {

    public String buildGreeting(String className, String name) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            Object instance = ctor.newInstance();
            Object result = clazz.getMethod("greet", String.class).invoke(instance, name);
            return (String) result;
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("reflection failed for " + className, e);
        }
    }
}