package chapter3.good;

import java.util.Objects;

public final class GoodPoint {
    private final int x;
    private final int y;
    private final int hashCode;

    public GoodPoint(int x, int y) {
        this.x = x;
        this.y = y;
        this.hashCode = Objects.hash(x, y);
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GoodPoint other)) {
            return false;
        }
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "GoodPoint[x=" + x + ", y=" + y + "]";
    }
}