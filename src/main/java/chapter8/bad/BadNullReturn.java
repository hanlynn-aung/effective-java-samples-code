package chapter8.bad;

import java.util.List;

public final class BadNullReturn {

    public List<String> find(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }
        return List.of("hit-" + query);
    }
}