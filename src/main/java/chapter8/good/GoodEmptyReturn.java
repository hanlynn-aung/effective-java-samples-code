package chapter8.good;

import java.util.List;

public final class GoodEmptyReturn {

    public List<String> find(String query) {
        if (query == null || query.isEmpty()) {
            return List.of();
        }
        return List.of("hit-" + query);
    }
}