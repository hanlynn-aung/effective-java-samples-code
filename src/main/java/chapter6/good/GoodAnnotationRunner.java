package chapter6.good;

import chapter6.good.GoodTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class GoodAnnotationRunner {

    public static final class Result {
        private final List<String> passed = new ArrayList<>();
        private final List<String> failed = new ArrayList<>();

        public int passed() {
            return passed.size();
        }

        public int failed() {
            return failed.size();
        }

        public List<String> failures() {
            return failed;
        }
    }

    public Result run(Object testee) throws Exception {
        Result result = new Result();
        for (Method method : testee.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(GoodTest.class)) {
                continue;
            }
            try {
                method.invoke(testee);
                result.passed.add(method.getName());
            } catch (InvocationTargetException wrapped) {
                result.failed.add(method.getName() + " -> " + wrapped.getCause());
            }
        }
        return result;
    }
}