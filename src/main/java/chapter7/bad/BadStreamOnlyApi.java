package chapter7.bad;

import java.util.stream.Stream;

public final class BadStreamOnlyApi {

    public static final class Tuple {
        public final String key;
        public final int value;

        public Tuple(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    // A reusable-by-convention API that nonetheless hands out a one-shot stream.
    private final Stream<Tuple> recentScores =
            Stream.of(new Tuple("alice", 10), new Tuple("bob", 8));

    public Stream<Tuple> recentScoresStream() {
        return recentScores;
    }
}