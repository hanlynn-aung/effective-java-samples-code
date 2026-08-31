package chapter9.bad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BadConcreteType {

    private final ArrayList<String> names;
    private final HashMap<String, Integer> scores;

    public BadConcreteType(ArrayList<String> names, HashMap<String, Integer> scores) {
        this.names = names;
        this.scores = scores;
    }

    public ArrayList<String> names() {
        return names;
    }

    public void add(String name) {
        names.add(name);
    }

    public int score(String name) {
        return scores.getOrDefault(name, 0);
    }
}