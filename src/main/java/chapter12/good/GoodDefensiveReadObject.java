package chapter12.good;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * A value range whose {@code readObject} is written defensively. It restores the
 * invariant (delegating to the validating constructor) and stores <em>copies</em>
 * of the mutable {@link Date} fields, so a caller who receives them from
 * {@code start()}/{@code end()} can never alias or mutate the internals.
 */
public final class GoodDefensiveReadObject implements Serializable {

    @Serial
    private static final long serialVersionUID = 777L;

    private Date start;
    private Date end;

    public GoodDefensiveReadObject(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.after(this.end)) {
            throw new IllegalArgumentException(this.start + " after " + this.end);
        }
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Date s = new Date(start.getTime());
        Date e = new Date(end.getTime());
        if (s.after(e)) {
            throw new java.io.InvalidObjectException("invalid period: " + s + " after " + e);
        }
        start = s;
        end = e;
    }

    public long durationMillis() {
        return end.getTime() - start.getTime();
    }

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }

    @Override
    public String toString() {
        return "GoodDefensiveReadObject [" + start + ", " + end + "]";
    }
}
