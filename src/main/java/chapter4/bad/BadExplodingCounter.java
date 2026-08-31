package chapter4.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadExplodingCounter extends FragileBaseCounter {
    private final List<String> bonuses;

    public BadExplodingCounter() {
        bonuses = new ArrayList<>();
    }

    @Override
    public void add(int amount) {
        super.add(amount + bonuses.size());
    }
}