package chapter9.good;

public final class GoodStringBuilder {

    public String repeat(String fragment, int times) {
        StringBuilder sb = new StringBuilder(fragment.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(fragment);
        }
        return sb.toString();
    }
}