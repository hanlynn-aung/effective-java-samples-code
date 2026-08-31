package chapter3.bad;

public final class BadHashPoint {
    private final int x;
    private final int y;

    public BadHashPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BadHashPoint other)) {
            return false;
        }
        return x == other.x && y == other.y;
    }
}