package chapter3.bad;

public final class BadCaseInsensitiveString {
    private final String value;

    public BadCaseInsensitiveString(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BadCaseInsensitiveString cis) {
            return value.equalsIgnoreCase(cis.value);
        }
        if (obj instanceof String s) {
            return value.equalsIgnoreCase(s);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }
}