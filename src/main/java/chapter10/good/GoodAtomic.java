package chapter10.good;

import java.util.ArrayList;
import java.util.List;

public final class GoodAtomic {

    private final List<String> items = new ArrayList<>();

    public void addBatch(List<String> batch) {
        // Validate everything first; only touch state once all checks pass.
        for (String item : batch) {
            if (item == null) {
                throw new IllegalArgumentException("null item in batch");
            }
        }
        items.addAll(batch);
    }

    public List<String> items() {
        return new ArrayList<>(items);
    }
}