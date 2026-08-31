package chapter8.good;

import java.util.Date;

public final class GoodPeriod {
    private final Date start;
    private final Date end;

    public GoodPeriod(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.after(this.end)) {
            throw new IllegalArgumentException("start must not be after end");
        }
    }

    public Date getStart() {
        return new Date(start.getTime());
    }

    public Date getEnd() {
        return new Date(end.getTime());
    }

    public long lengthMillis() {
        return end.getTime() - start.getTime();
    }
}