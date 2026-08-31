package chapter6.good;

public final class GoodShape {

    public static final class Square extends GoodShapeBase {
        @Override
        public String name() {
            return "square";
        }
    }
}