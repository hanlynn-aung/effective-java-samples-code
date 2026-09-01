package chapter10;

import chapter10.bad.BadCheckedForBug;
import chapter10.good.GoodCheckedRuntime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckedVsRuntimeTest {

    @Test
    @DisplayName("Bad: a programming error (bad args) is forced as a checked exception")
    void badMakesProgrammingErrorsChecked() throws BadCheckedForBug.ProgrammingError {
        BadCheckedForBug bad = new BadCheckedForBug();
        assertThrows(BadCheckedForBug.ProgrammingError.class, () -> bad.divide(1, 0));
        assertThrows(BadCheckedForBug.ProgrammingError.class, () -> bad.setAge(-1));
    }

    @Test
    @DisplayName("Good: recoverable conditions are checked; programmer errors are runtime")
    void goodCheckedVsRuntime() throws GoodCheckedRuntime.InsufficientFundsException {
        GoodCheckedRuntime good = new GoodCheckedRuntime();
        // Recoverable: caller must handle -> checked exception.
        assertThrows(GoodCheckedRuntime.InsufficientFundsException.class,
                () -> good.withdraw(10, 100));
        // Programming errors: no catch required.
        assertThrows(IllegalArgumentException.class, () -> good.divide(1, 0));
        assertThrows(IllegalArgumentException.class, () -> good.setAge(-1));
    }
}