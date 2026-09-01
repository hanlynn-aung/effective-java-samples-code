package chapter11.good;

import java.util.Collections;
import java.util.Map;

/**
 * Lazy-initialised field using the double-checked locking idiom with a volatile
 * field - correct and reasonably performant.
 */
public final class GoodLazyInit {

    private volatile Map<String, String> heavy;

    public Map<String, String> getHeavy() {
        Map<String, String> result = heavy;
        if (result == null) {
            synchronized (this) {
                result = heavy;
                if (result == null) {
                    result = build();
                    heavy = result;
                }
            }
        }
        return result;
    }

    private Map<String, String> build() {
        return Collections.singletonMap("expensive", "value");
    }
}