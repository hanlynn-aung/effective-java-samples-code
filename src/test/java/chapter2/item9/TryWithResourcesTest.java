package chapter2.item9;

import chapter2.item9.bad.BadTryFinallyFileReader;
import chapter2.item9.good.GoodTryWithResourcesFileReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TryWithResourcesTest {

    private static final class BoomReader extends Reader {
        private int closeCount;

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            throw new IOException("read boom");
        }

        @Override
        public void close() throws IOException {
            closeCount++;
            throw new IOException("close boom");
        }
    }

    @Test
    @DisplayName("Bad: try/finally lets the close failure mask the primary exception")
    void badTryFinallyMasksPrimaryException() {
        IOException ex = assertThrows(IOException.class,
                () -> new BadTryFinallyFileReader().firstLine(new BoomReader()));
        assertEquals("close boom", ex.getMessage());
        assertEquals(0, ex.getSuppressed().length);
    }

    @Test
    @DisplayName("Good: try-with-resources surfaces the primary exception")
    void goodTryWithResourcesSurfacesPrimaryException() {
        IOException ex = assertThrows(IOException.class,
                () -> new GoodTryWithResourcesFileReader().firstLine(new BoomReader()));
        assertEquals("read boom", ex.getMessage());
    }

    @Test
    @DisplayName("Good: close failure is kept as a suppressed exception")
    void goodCloseFailureIsSuppressed() {
        IOException ex = assertThrows(IOException.class,
                () -> new GoodTryWithResourcesFileReader().firstLine(new BoomReader()));
        assertEquals(1, ex.getSuppressed().length);
        assertEquals("close boom", ex.getSuppressed()[0].getMessage());
    }

    @Test
    @DisplayName("Both patterns eventually close the resource")
    void bothCloseTheResource() throws Exception {
        BoomReader badSource = new BoomReader();
        assertThrows(IOException.class,
                () -> new BadTryFinallyFileReader().firstLine(badSource));
        assertEquals(1, badSource.closeCount);

        BoomReader goodSource = new BoomReader();
        assertThrows(IOException.class,
                () -> new GoodTryWithResourcesFileReader().firstLine(goodSource));
        assertEquals(1, goodSource.closeCount);
    }
}