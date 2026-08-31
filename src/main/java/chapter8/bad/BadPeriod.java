package chapter8.bad;

import java.util.Date;

public final class BadPeriod {
    private final Date start;
    private final Date end;

    public BadPeriod(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public long lengthMillis() {
        return end.getTime() - start.getTime();
    }
}