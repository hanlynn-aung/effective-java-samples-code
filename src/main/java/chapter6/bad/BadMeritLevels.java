package chapter6.bad;

public final class BadMeritLevels {

    public enum BadMerit {
        LOW, HIGH, MEDIUM;

        public int rank() {
            return ordinal() + 1;
        }
    }
}