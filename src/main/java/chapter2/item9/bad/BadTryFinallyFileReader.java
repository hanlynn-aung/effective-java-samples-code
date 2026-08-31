package chapter2.item9.bad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class BadTryFinallyFileReader {
    public String firstLine(Reader source) throws IOException {
        BufferedReader reader = new BufferedReader(source);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }
}
