package chapter7.good;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class GoodCollectionApi {

    public static final class Tuple {
        public final String key;
        public final int value;

        public Tuple(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final List<Tuple> SCORES = List.of(
            new Tuple("alice", 10),
            new Tuple("bob", 8));

    public Collection<Tuple> recentScores() {
        return Collections.unmodifiableList(SCORES);
    }

    public Stream<Tuple> recentScoresStream() {
        return SCORES.stream();
    }
}