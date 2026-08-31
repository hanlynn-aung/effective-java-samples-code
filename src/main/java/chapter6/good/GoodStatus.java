package chapter6.good;

public enum GoodStatus {
    READY("ready"), FAILED("failed");

    private final String description;

    GoodStatus(String description) { this.description = description; }
    public String description() { return description; }
}
