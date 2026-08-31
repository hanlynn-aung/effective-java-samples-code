package chapter7;

import chapter7.bad.BadStreamSpaghetti;
import chapter7.good.GoodClearStreams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamJudiciouslyTest {

    private static final String[] LINES = {
            "Hello World",
            "Java 17 Streams",
            "One Two"
    };

    @Test
    @DisplayName("Spaghetti chain and clear loop produce the same letter counts")
    void bothApproachesAgree() {
        Map<String, Long> bad = new BadStreamSpaghetti().letterCountsAcrossLines(LINES);
        Map<Character, Long> good = new GoodClearStreams().letterCounts(LINES);

        Map<String, Long> expected = new HashMap<>();
        for (String line : LINES) {
            for (char c : line.toCharArray()) {
                if (Character.isLetter(c)) {
                    String key = String.valueOf(Character.toLowerCase(c));
                    expected.merge(key, 1L, Long::sum);
                }
            }
        }
        assertEquals(expected, bad);
        assertEquals(expected.keySet().size(), good.size());
    }
}