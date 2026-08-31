package chapter3.bad;

public class BadTransitivityPoint {
    private final int x;
    private final int y;

    public BadTransitivityPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BadTransitivityPoint other)) {
            return false;
        }
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}