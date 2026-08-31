package chapter9.bad;

public final class BadConcatLoop {

    public String repeat(String fragment, int times) {
        String result = "";
        for (int i = 0; i < times; i++) {
            result += fragment;
        }
        return result;
    }
}