package chapter7.good;

import java.util.HashMap;
import java.util.Map;

public final class GoodClearStreams {

    public Map<Character, Long> letterCounts(String[] lines) {
        Map<Character, Long> counts = new HashMap<>();
        for (String line : lines) {
            for (char c : line.toCharArray()) {
                if (Character.isLetter(c)) {
                    char lower = Character.toLowerCase(c);
                    counts.merge(lower, 1L, Long::sum);
                }
            }
        }
        return counts;
    }
}