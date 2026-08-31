package chapter9.good;

public final class GoodTypedEnum {

    public enum Status { PENDING, READY, DONE }

    private Status status;

    public GoodTypedEnum(Status status) {
        this.status = status;
    }

    public Status status() {
        return status;
    }

    public boolean isReady() {
        return status == Status.READY;
    }

    public void advance() {
        if (status == Status.PENDING) {
            status = Status.READY;
        }
    }
}