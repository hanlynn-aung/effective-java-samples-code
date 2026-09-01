package chapter11;

import chapter11.bad.BadRawThread;
import chapter11.good.GoodExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutorTest {

    @Test
    @DisplayName("Both: run the same number of tasks")
    void bothRunTasks() throws Exception {
        assertEquals(5, new BadRawThread().runPyramid(5));
    }

    @Test
    @DisplayName("Good: the executor returns a computed result via Future (raw threads can't)")
    void goodReturnsResults() throws Exception {
        // 0^2+1^2+2^2+3^2+4^2 = 0+1+4+9+16 = 30
        assertEquals(30, new GoodExecutor().runPyramid(5));
    }
}