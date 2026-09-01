package chapter12.bad;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * (bad) Serializes the real class directly. The class's invariant
 * ({@code start <= end}) is enforced by the constructor only, so a crafted
 * stream can deserialize an "impossible" {@code Period}, and the serialized form
 * is tied to the mutable internal {@link Date} representation.
 */
public final class BadDirectSerialization implements Serializable {

    private final Date start;
    private final Date end;

    public BadDirectSerialization(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.after(this.end)) {
            throw new IllegalArgumentException(this.start + " after " + this.end);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public long durationMillis() {
        return end.getTime() - start.getTime();
    }

    @Override
    public String toString() {
        return "BadDirectSerialization [" + start + ", " + end + "]";
    }
}