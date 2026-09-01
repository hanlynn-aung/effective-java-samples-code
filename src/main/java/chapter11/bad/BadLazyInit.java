package chapter11.bad;

import java.util.HashMap;
import java.util.Map;

public final class BadLazyInit {

    private Map<String, String> heavy;   // NOT volatile, lazy, unsynchronized

    public Map<String, String> getHeavy() {
        if (heavy == null) {
            Map<String, String> local = new HashMap<>();
            local.put("expensive", build());
            heavy = local;
        }
        return heavy;
    }

    private String build() {
        return "value";
    }
}