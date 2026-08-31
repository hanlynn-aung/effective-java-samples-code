package chapter6.good;

import chapter6.good.GoodTest;

public final class GoodAnnotatedCarrier {

    @GoodTest
    public void adds() {
    }

    @GoodTest
    public void getsSum() {
    }

    @GoodTest
    public void multiplyFails() {
        throw new IllegalStateException("boom");
    }
}