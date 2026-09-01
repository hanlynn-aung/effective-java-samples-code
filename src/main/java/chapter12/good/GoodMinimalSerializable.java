package chapter12.good;

import java.io.Serial;
import java.io.Serializable;

/**
 * Implements {@link Serializable} only after deliberately weighing the costs:
 * the class is immutable, its representation is stable, and it declares an
 * explicit {@link Serial} {@code serialVersionUID} so evolution is controlled.
 * If a value type does not *need* streaming persistence, it should not be
 * serializable at all.
 */
public final class GoodMinimalSerializable implements Serializable {

    @Serial
    private static final long serialVersionUID = 20260901L;

    private final String title;

    public GoodMinimalSerializable(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

    @Override
    public String toString() {
        return "GoodMinimalSerializable{title='" + title + "'}";
    }
}