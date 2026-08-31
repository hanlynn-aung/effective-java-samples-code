package chapter9.good;

import java.math.BigDecimal;

public final class GoodDollarsBigDecimal {

    private BigDecimal balance;

    public GoodDollarsBigDecimal(String initial) {
        this.balance = new BigDecimal(initial);
    }

    public void add(String amount) {
        balance = balance.add(new BigDecimal(amount));
    }

    public BigDecimal balance() {
        return balance;
    }
}