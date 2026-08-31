package chapter8;

import chapter8.bad.BadDeposit;
import chapter8.good.GoodDeposit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterValidationTest {

    @Test
    @DisplayName("Bad: a deposit silently accepts negative and NaN amounts")
    void badAcceptsInvalidAmounts() {
        BadDeposit account = new BadDeposit();
        account.deposit(-5);
        account.deposit(Double.NaN);
        assertEquals(Double.NaN, account.balance());
    }

    @Test
    @DisplayName("Good: the constructor rejects a negative initial balance")
    void goodConstructorValidates() {
        assertThrows(IllegalArgumentException.class, () -> new GoodDeposit(-1));
        assertThrows(IllegalArgumentException.class, () -> new GoodDeposit(Double.NaN));
    }

    @Test
    @DisplayName("Good: deposit refuses non-positive amounts before touching state")
    void goodDepositValidatesBeforeMutating() {
        GoodDeposit account = new GoodDeposit(100);
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-5));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(Double.NaN));
        assertEquals(100, account.balance());
    }

    @Test
    @DisplayName("Good: valid deposits accumulate correctly")
    void goodValidDepositWorks() {
        GoodDeposit account = new GoodDeposit(100);
        account.deposit(2.5);
        assertEquals(102.5, account.balance());
    }
}