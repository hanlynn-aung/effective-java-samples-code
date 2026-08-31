package chapter7.good;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class GoodConcise {

    public long countShorts(String[] words) {
        List<String> sorted = new ArrayList<>(java.util.Arrays.asList(words));
        sorted.sort(Comparator.comparingInt(String::length));
        return sorted.stream().filter(s -> s.length() < 5).count();
    }

    public Function<String, Integer> lengthOf() {
        return String::length;
    }

    public List<String> toList(String[] words) {
        return java.util.Arrays.stream(words).collect(Collectors.toList());
    }
}