package chapter7;

import chapter7.bad.BadStreamOnlyApi;
import chapter7.good.GoodCollectionApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionReturnTest {

    @Test
    @DisplayName("Bad: returning a Stream makes it a one-shot, non-reusable value")
    void badStreamIsOneShot() {
        BadStreamOnlyApi api = new BadStreamOnlyApi();
        Stream<BadStreamOnlyApi.Tuple> stream = api.recentScoresStream();
        assertEquals(2, stream.count());
        // The stream is exhausted; reading it again throws IllegalStateException.
        assertThrows(IllegalStateException.class, stream::count);
    }

    @Test
    @DisplayName("Good: returning a Collection lets callers re-iterate and query size freely")
    void goodCollectionIsReusable() {
        GoodCollectionApi api = new GoodCollectionApi();
        Collection<GoodCollectionApi.Tuple> scores = api.recentScores();
        assertEquals(2, scores.size());
        assertEquals(2, scores.size());
        List<String> keys = scores.stream().map(t -> t.key).collect(Collectors.toList());
        assertTrue(keys.containsAll(List.of("alice", "bob")));
    }
}