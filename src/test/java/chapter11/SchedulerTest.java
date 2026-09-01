package chapter11;

import chapter11.bad.BadBusyWait;
import chapter11.good.GoodSchedulerNeutral;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulerTest {

    @Test
    @DisplayName("Bad: busy-wait with yield depends on timing but eventually observes the flag")
    void badBusyWaitEventuallyDone() {
        BadBusyWait bad = new BadBusyWait();
        bad.startWork();
        boolean done = bad.waitForDone();
        assertTrue(done);
    }

    @Test
    @DisplayName("Good: a CountDownLatch blocks efficiently and honours a timeout, never hangs")
    void goodLatchCompletes() throws InterruptedException {
        GoodSchedulerNeutral good = new GoodSchedulerNeutral();
        // The worker finishes in ~50ms, so a generous timeout succeeds.
        assertTrue(good.startAndWait(5_000));
    }

    @Test
    @DisplayName("Good: a too-short timeout fails cleanly instead of hanging forever")
    void goodLatchTimesOut() throws InterruptedException {
        GoodSchedulerNeutral good = new GoodSchedulerNeutral();
        assertFalse(good.startAndWait(1));
    }
}