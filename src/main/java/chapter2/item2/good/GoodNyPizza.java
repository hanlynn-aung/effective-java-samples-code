package chapter2.item2.good;

import java.util.Objects;

public class GoodNyPizza extends GoodPizza {
    public enum Size { SMALL, MEDIUM, LARGE }

    private final Size size;

    private GoodNyPizza(Builder builder) {
        super(builder);
        this.size = builder.size;
    }

    public Size size() {
        return size;
    }

    public static class Builder extends GoodPizza.Builder<Builder> {
        private final Size size;

        public Builder(Size size) {
            this.size = Objects.requireNonNull(size, "size");
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public GoodNyPizza build() {
            return new GoodNyPizza(this);
        }
    }
}