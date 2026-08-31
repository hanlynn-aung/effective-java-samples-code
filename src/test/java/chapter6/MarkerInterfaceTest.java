package chapter6;

import chapter6.bad.BadAnnotatedOnlyPersistence;
import chapter6.bad.BadPersistable;
import chapter6.good.GoodPersistable;
import chapter6.good.GoodRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkerInterfaceTest {

    @Test
    @DisplayName("Good: a marker interface is a real type - save() only accepts it")
    void goodMarkerIsAType() {
        GoodRepository repo = new GoodRepository();
        GoodPersistable entity = new GoodPersistable() { };
        repo.save(entity);
        assertTrue(repo.hasSaved(entity));
        // save(GoodPersistable) rejects a non-persistable object at compile time.
    }

    @BadPersistable
    static final class AnnotatedEntity {
    }

    @Test
    @DisplayName("Bad: the annotation marker is just a hint - enforcement is manual and late")
    void badAnnotationMarkerIsCheckedLate() {
        BadAnnotatedOnlyPersistence persistence = new BadAnnotatedOnlyPersistence();
        persistence.save(new AnnotatedEntity());
        assertEquals(1, persistence.size());
    }

    @Test
    @DisplayName("Bad: an unannotated object slips past until the manual runtime check")
    void badUnannotatedIsRejectedAtRuntime() {
        BadAnnotatedOnlyPersistence persistence = new BadAnnotatedOnlyPersistence();
        assertThrows(IllegalArgumentException.class,
                () -> persistence.save(new Object()));
    }
}