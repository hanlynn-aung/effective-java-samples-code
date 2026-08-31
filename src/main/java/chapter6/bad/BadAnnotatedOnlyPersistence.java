package chapter6.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadAnnotatedOnlyPersistence {
    private final List<Object> stored = new ArrayList<>();

    public void save(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        if (!entity.getClass().isAnnotationPresent(BadPersistable.class)) {
            throw new IllegalArgumentException(
                    entity.getClass().getSimpleName() + " is not @BadPersistable");
        }
        stored.add(entity);
    }

    public int size() {
        return stored.size();
    }
}