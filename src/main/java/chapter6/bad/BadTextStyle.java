package chapter6.bad;

public final class BadTextStyle {
    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_BOLD = 1 << 0;
    public static final int STYLE_ITALIC = 1 << 1;
    public static final int STYLE_UNDERLINE = 1 << 2;

    public String apply(int styles) {
        StringBuilder result = new StringBuilder("text");
        if ((styles & STYLE_BOLD) != 0) {
            result.append("+bold");
        }
        if ((styles & STYLE_ITALIC) != 0) {
            result.append("+italic");
        }
        if ((styles & STYLE_UNDERLINE) != 0) {
            result.append("+underline");
        }
        return result.toString();
    }
}