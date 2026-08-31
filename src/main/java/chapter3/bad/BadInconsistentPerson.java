package chapter3.bad;

public final class BadInconsistentPerson implements Comparable<BadInconsistentPerson> {
    private final String name;
    private final int score;

    public BadInconsistentPerson(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String name() { return name; }
    public int score() { return score; }

    @Override
    public int compareTo(BadInconsistentPerson other) {
        return name.compareTo(other.name);
    }
}