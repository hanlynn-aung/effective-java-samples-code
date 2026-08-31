package chapter6.good;

import java.util.ArrayList;
import java.util.List;

public final class GoodRepository {
    private final List<GoodPersistable> stored = new ArrayList<>();

    public void save(GoodPersistable entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        stored.add(entity);
    }

    public boolean hasSaved(Object entity) {
        return stored.contains(entity);
    }
}