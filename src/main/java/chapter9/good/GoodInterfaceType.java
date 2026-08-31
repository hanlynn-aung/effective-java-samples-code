package chapter9.good;

import java.util.List;
import java.util.Map;

public final class GoodInterfaceType {

    private final List<String> names;
    private final Map<String, Integer> scores;

    public GoodInterfaceType(List<String> names, Map<String, Integer> scores) {
        this.names = names;
        this.scores = scores;
    }

    public List<String> names() {
        return names;
    }

    public void add(String name) {
        names.add(name);
    }

    public int score(String name) {
        return scores.getOrDefault(name, 0);
    }
}