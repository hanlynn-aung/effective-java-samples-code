package chapter4.bad;

public final class BadMutableTime {
    private int hour;
    private int minute;

    public BadMutableTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int hour() {
        return hour;
    }

    public int minute() {
        return minute;
    }

    @Override
    public String toString() {
        return "BadMutableTime[" + hour + ":" + minute + "]";
    }
}