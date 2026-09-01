package chapter12.good;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Uses a <em>serialization proxy</em> instead of serializing the real instance.
 * The real class never implements serialization of its internals; instead a
 * private static inner proxy carries only the logical {@code long} fields and
 * {@code readResolve} rebuilds a fully-validated {@code Period} through the
 * normal constructor. This guarantees the class invariant can never be violated
 * by a crafted stream, no matter how the internal representation evolves.
 */
public final class GoodSerializationProxy implements Serializable {

    @Serial
    private static final long serialVersionUID = 42L;

    private final Date start;
    private final Date end;

    public GoodSerializationProxy(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.after(this.end)) {
            throw new IllegalArgumentException(this.start + " after " + this.end);
        }
    }

    public long durationMillis() {
        return end.getTime() - start.getTime();
    }

    @Serial
    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(start.getTime(), end.getTime());
    }

    @Serial
    private void readObject(ObjectInputStream in) throws InvalidObjectException {
        // A defensive hack: this class must never be read directly, only via its proxy.
        throw new InvalidObjectException("proxy required");
    }

    private static final class SerializationProxy implements Serializable {

        @Serial
        private static final long serialVersionUID = 43L;

        private final long startMillis;
        private final long endMillis;

        private SerializationProxy(long startMillis, long endMillis) {
            this.startMillis = startMillis;
            this.endMillis = endMillis;
        }

        @Serial
        private Object readResolve() throws ObjectStreamException {
            try {
                return new GoodSerializationProxy(new Date(startMillis), new Date(endMillis));
            } catch (IllegalArgumentException ex) {
                throw new InvalidObjectException("invalid period: " + ex.getMessage());
            }
        }
    }
}