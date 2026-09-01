package chapter12.bad;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * (bad) A value range whose {@code readObject} is not written defensively. On
 * deserialization it relies on the default read, so a crafted stream can
 * violate the class invariant ({@code start <= end}) and hand back an
 * "impossible" {@code Period}. The internal {@link Date} objects are also
 * mutable and shared, so callers can corrupt the instance by mutating what
 * {@code start()}/{@code end()} return.
 */
public final class BadUnvalidatedReadObject implements Serializable {

    private final Date start;
    private final Date end;

    public BadUnvalidatedReadObject(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (start.after(end)) {
            throw new IllegalArgumentException(start + " after " + end);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // No validation and no defensive copy: the invariant can be broken and
        // the mutable Dates aliased.
        in.defaultReadObject();
    }

    public long durationMillis() {
        return end.getTime() - start.getTime();
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }

    @Override
    public String toString() {
        return "BadUnvalidatedReadObject [" + start + ", " + end + "]";
    }
}