package chapter7.bad;

public final class BadVerbose {

    public long countShorts(String[] words) {
        java.util.Arrays.sort(words, (a, b) -> Integer.compare(a.length(), b.length()));
        java.util.stream.Stream<String> stream = java.util.Arrays.stream(words);
        return stream.filter(s -> s.length() < 5).count();
    }
}