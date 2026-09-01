package chapter9.bad;

public final class BadNaming {

    private final int n;
    private int x;

    public BadNaming(int n) {
        this.n = n;
    }

    public int gv() {
        return n;
    }

    public void st(int v) {
        this.x = v;
    }

    public int gt() {
        return x;
    }

    public boolean chk() {
        return x >= n;
    }
}