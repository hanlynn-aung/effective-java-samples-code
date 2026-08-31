package chapter7.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadStatefulCollect {

    public List<String> uppercaseTopWords(String[] words, int limit) {
        List<String> bucket = new ArrayList<>();
        java.util.Arrays.stream(words).limit(limit)
                .forEach(w -> bucket.add(w.toUpperCase()));
        return bucket;
    }
}