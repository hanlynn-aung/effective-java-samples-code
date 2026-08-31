package chapter9.good;

import java.util.Collection;
import java.util.List;

public final class GoodForEach {

    private final Collection<String> names;

    public GoodForEach(Collection<String> names) {
        this.names = names;
    }

    public int countEmpty() {
        int count = 0;
        for (String name : names) {
            if (name.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}