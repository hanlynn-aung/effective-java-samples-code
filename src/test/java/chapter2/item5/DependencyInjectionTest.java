package chapter2.item5;

import chapter2.item5.bad.BadHardwiredReportService;
import chapter2.item5.bad.BadServiceLocatorReportService;
import chapter2.item5.good.GoodInjectedReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DependencyInjectionTest {

    private static final class FakeRepository
            implements GoodInjectedReportService.ReportRepository {
        private int calls;

        @Override
        public String load() {
            calls++;
            return "fake report";
        }
    }

    @Test
    @DisplayName("Good: injected service actually uses the injected resource")
    void goodServiceUsesInjectedResource() {
        FakeRepository fake = new FakeRepository();
        GoodInjectedReportService service = new GoodInjectedReportService(fake);
        assertEquals("fake report", service.report());
        assertEquals(1, fake.calls);
    }

    @Test
    @DisplayName("Good: injected service swaps implementation freely")
    void goodServiceSwapsImplementation() {
        GoodInjectedReportService service = new GoodInjectedReportService(
                () -> "different impl");
        assertEquals("different impl", service.report());
    }

    @Test
    @DisplayName("Good: injected service rejects a null resource")
    void goodServiceRejectsNullResource() {
        assertThrows(NullPointerException.class,
                () -> new GoodInjectedReportService(null));
    }

    @Test
    @DisplayName("Bad: hardwired service cannot be swapped or faked")
    void badHardwiredServiceCannotBeSwapped() {
        assertEquals("file report", new BadHardwiredReportService().report());
    }

    @Test
    @DisplayName("Bad: service locator hides its dependency in global state")
    void badServiceLocatorUsesGlobalState() {
        BadServiceLocatorReportService.register(() -> "locator report");
        assertEquals("locator report",
                BadServiceLocatorReportService.create().report());
    }
}