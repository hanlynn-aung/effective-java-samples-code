package chapter5.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadNumbers {
    public List values() {
        List values = new ArrayList();
        values.add(42);
        values.add("not a number");
        return values;
    }
}
