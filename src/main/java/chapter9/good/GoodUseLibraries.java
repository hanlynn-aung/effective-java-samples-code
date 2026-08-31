package chapter9.good;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class GoodUseLibraries {

    public int randomWithRange(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public String join(List<String> parts) {
        return String.join("", parts);
    }

    public double max(double a, double b) {
        return Math.max(a, b);
    }
}