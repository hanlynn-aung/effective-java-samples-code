package chapter2.item2.good;

public class GoodCalzone extends GoodPizza {
    private final boolean sauceInside;

    private GoodCalzone(Builder builder) {
        super(builder);
        this.sauceInside = builder.sauceInside;
    }

    public boolean sauceInside() {
        return sauceInside;
    }

    public static class Builder extends GoodPizza.Builder<Builder> {
        private boolean sauceInside;

        public Builder sauceInside() {
            sauceInside = true;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public GoodCalzone build() {
            return new GoodCalzone(this);
        }
    }
}