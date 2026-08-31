package chapter2.item4.good;

public final class GoodUtilityClass {
    private GoodUtilityClass() {
        throw new AssertionError("No instances");
    }

    public static String normalize(String value) {
        return value.trim().toLowerCase();
    }
}
