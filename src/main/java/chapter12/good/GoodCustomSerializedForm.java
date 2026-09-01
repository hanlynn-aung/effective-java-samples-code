package chapter12.good;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Same value range as the bad version, but with a <em>custom serialized form</em>:
 * the stream stores only the logical data (two {@code long} epoch-millis) as
 * primitives, decoupled from the internal {@link Date} representation. The
 * internal representation can change freely without breaking the stream, the
 * bytes are the minimum needed, and {@code readObject} re-validates the
 * invariant.
 */
public final class GoodCustomSerializedForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 9001L;

    private long startMillis;
    private long endMillis;

    public GoodCustomSerializedForm(Date start, Date end) {
        this(start.getTime(), end.getTime());
    }

    private GoodCustomSerializedForm(long startMillis, long endMillis) {
        if (startMillis > endMillis) {
            throw new IllegalArgumentException("start after end");
        }
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeLong(startMillis);
        out.writeLong(endMillis);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException {
        long start = in.readLong();
        long end = in.readLong();
        if (start > end) {
            throw new java.io.InvalidObjectException("start after end");
        }
        startMillis = start;
        endMillis = end;
    }

    public long durationMillis() {
        return endMillis - startMillis;
    }

    public Date start() {
        return new Date(startMillis);
    }

    public Date end() {
        return new Date(endMillis);
    }

    @Override
    public String toString() {
        return "GoodCustomSerializedForm [" + start() + ", " + end() + "]";
    }
}