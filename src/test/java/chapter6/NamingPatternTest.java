package chapter6;

import chapter6.bad.BadNamingCarrier;
import chapter6.bad.BadNamingRunner;
import chapter6.good.GoodAnnotatedCarrier;
import chapter6.good.GoodAnnotationRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NamingPatternTest {

    @Test
    @DisplayName("Bad: a typo'd test name is silently skipped - no error anywhere")
    void badNamingSkipsTyposSilently() throws Exception {
        BadNamingRunner runner = new BadNamingRunner();
        int passed = runner.run(new BadNamingCarrier());
        assertEquals(2, passed, "only test* methods run; the typo'l method never runs and never throws");
    }

    @Test
    @DisplayName("Good: @GoodTest methods are discovered by annotation, not names")
    void goodAnnotationFindsAllAnnotated() throws Exception {
        GoodAnnotationRunner runner = new GoodAnnotationRunner();
        GoodAnnotationRunner.Result result = runner.run(new GoodAnnotatedCarrier());
        assertEquals(3, result.passed() + result.failed());
        assertEquals(2, result.passed());
        assertEquals(1, result.failed());
        assertTrue(result.failures().get(0).contains("multiplyFails"));
    }
}