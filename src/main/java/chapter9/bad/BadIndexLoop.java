package chapter9.bad;

import java.util.List;

public final class BadIndexLoop {

    private final List<String> names;

    public BadIndexLoop(List<String> names) {
        this.names = names;
    }

    public int countEmpty() {
        int count = 0;
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }
}