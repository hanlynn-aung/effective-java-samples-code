package chapter6.bad;

public enum BadFrozenOperation {
    PLUS, MINUS, TIMES, DIVIDE;

    public double apply(double x, double y) {
        return switch (this) {
            case PLUS -> x + y;
            case MINUS -> x - y;
            case TIMES -> x * y;
            case DIVIDE -> x / y;
        };
    }
}