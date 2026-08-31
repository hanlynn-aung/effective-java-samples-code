package chapter3.good;

import java.util.Objects;

public final class GoodComparablePerson implements Comparable<GoodComparablePerson> {
    private final String name;
    private final int score;

    public GoodComparablePerson(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String name() { return name; }
    public int score() { return score; }

    @Override
    public int compareTo(GoodComparablePerson other) {
        int byScore = Integer.compare(score, other.score);
        if (byScore != 0) {
            return byScore;
        }
        return name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GoodComparablePerson other)) {
            return false;
        }
        return score == other.score && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }

    @Override
    public String toString() {
        return "GoodComparablePerson[name=" + name + ", score=" + score + "]";
    }
}