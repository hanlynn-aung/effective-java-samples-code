package chapter10.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadNonAtomic {

    private final List<String> items = new ArrayList<>();

    public void addBatch(List<String> batch) {
        for (String item : batch) {
            if (item == null) {
                throw new IllegalArgumentException("null item in batch");
            }
            items.add(item);
        }
    }

    public List<String> items() {
        return new ArrayList<>(items);
    }
}