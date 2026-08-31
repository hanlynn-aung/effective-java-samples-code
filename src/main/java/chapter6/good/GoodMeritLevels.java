package chapter6.good;

public final class GoodMeritLevels {

    public enum GoodMerit {
        LOW(1), HIGH(3), MEDIUM(2);

        private final int tier;

        GoodMerit(int tier) {
            this.tier = tier;
        }

        public int tier() {
            return tier;
        }
    }
}