package chapter9.bad;

public final class BadStringState {

    private String state;

    public BadStringState(String state) {
        this.state = state;
    }

    public String state() {
        return state;
    }

    public boolean isReady() {
        return "READY".equals(state) || "ready".equals(state);
    }

    public void advance() {
        if ("PENDING".equals(state) || "pending".equals(state)) {
            state = "READY";
        }
    }
}