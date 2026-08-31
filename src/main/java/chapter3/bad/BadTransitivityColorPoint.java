package chapter3.bad;

public final class BadTransitivityColorPoint extends BadTransitivityPoint {
    private final String color;

    public BadTransitivityColorPoint(int x, int y, String color) {
        super(x, y);
        this.color = color;
    }

    public String color() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BadTransitivityColorPoint other)) {
            return false;
        }
        return super.equals(other) && color.equals(other.color);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + color.hashCode();
    }
}