package chapter7.good;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class GoodCollectors {

    public List<String> uppercaseTopWords(String[] words, int limit) {
        return Arrays.stream(words)
                .limit(limit)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }
}