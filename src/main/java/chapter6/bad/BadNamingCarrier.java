package chapter6.bad;

public final class BadNamingCarrier {
    public boolean success = true;

    public void testAdds() {
        success = success;
    }

    public void testGetsSum() {
        success = success;
    }

    public void tetsMultiply() {
        throw new IllegalStateException("typo'd test: never ran, so never caught");
    }
}