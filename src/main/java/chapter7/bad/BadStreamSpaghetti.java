package chapter7.bad;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BadStreamSpaghetti {

    public Map<String, Long> letterCountsAcrossLines(String[] lines) {
        return java.util.Arrays.stream(lines).flatMap(line -> line.chars().boxed())
                .filter(c -> Character.isLetter(c)).map(c -> String.valueOf((char) c.intValue()))
                .map(String::toLowerCase).collect(Collectors.groupingBy(Function.identity(),
                        Collectors.counting()));
    }
}