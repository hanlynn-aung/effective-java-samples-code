package chapter12.bad;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * (bad) A value range that relies on the <em>default</em> serialized form.
 * Without an explicit {@code writeObject}/{@code readObject}, serialization
 * writes the internal {@link Date} objects directly: the stream format is tied
 * to the internal representation (change {@code start} from {@code Date} to an
 * epoch {@code long} and old bytes break), it can be larger than the logical
 * data needs, and it can leak redundant/derived fields.
 */
public final class BadDefaultSerializedForm implements Serializable {

    private final Date start;
    private final Date end;

    public BadDefaultSerializedForm(Date start, Date end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        if (start.after(end)) {
            throw new IllegalArgumentException(start + " after " + end);
        }
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BadDefaultSerializedForm that)) {
            return false;
        }
        return start.equals(that.start) && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return 31 * start.hashCode() + end.hashCode();
    }

    @Override
    public String toString() {
        return "BadDefaultSerializedForm [" + start + ", " + end + "]";
    }
}