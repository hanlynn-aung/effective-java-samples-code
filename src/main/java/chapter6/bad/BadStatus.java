package chapter6.bad;

public final class BadStatus {
    public static final int READY = 1;
    public static final int FAILED = 2;

    public String describe(int status) {
        return status == READY ? "ready" : status == FAILED ? "failed" : "unknown";
    }
}
