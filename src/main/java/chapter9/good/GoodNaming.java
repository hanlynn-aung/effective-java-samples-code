package chapter9.good;

public final class GoodNaming {

    private static final int MINIMUM = 0;

    private final int limit;
    private int value;

    public GoodNaming(int limit) {
        if (limit < MINIMUM) {
            throw new IllegalArgumentException("limit must be non-negative");
        }
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isAtOrAboveLimit() {
        return value >= limit;
    }
}