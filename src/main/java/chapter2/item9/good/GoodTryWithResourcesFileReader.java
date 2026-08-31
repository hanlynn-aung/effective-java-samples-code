package chapter2.item9.good;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class GoodTryWithResourcesFileReader {
    public String firstLine(Reader source) throws IOException {
        try (BufferedReader reader = new BufferedReader(source)) {
            return reader.readLine();
        }
    }
}
