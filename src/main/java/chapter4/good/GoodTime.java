package chapter4.good;

public final class GoodTime {
    private final int hour;
    private final int minute;
    private final int hashCode;

    public GoodTime(int hour, int minute) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("hour out of range: " + hour);
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("minute out of range: " + minute);
        }
        this.hour = hour;
        this.minute = minute;
        this.hashCode = 31 * hour + minute;
    }

    public int hour() {
        return hour;
    }

    public int minute() {
        return minute;
    }

    public GoodTime withHour(int newHour) {
        return new GoodTime(newHour, minute);
    }

    public GoodTime withMinute(int newMinute) {
        return new GoodTime(hour, newMinute);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GoodTime other)) {
            return false;
        }
        return hour == other.hour && minute == other.minute;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("GoodTime[%02d:%02d]", hour, minute);
    }
}