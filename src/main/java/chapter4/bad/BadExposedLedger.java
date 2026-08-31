package chapter4.bad;

import java.util.HashMap;
import java.util.Map;

public final class BadExposedLedger {
    public Map<String, Long> entries = new HashMap<>();

    public void record(String account, long amount) {
        entries.put(account, entries.getOrDefault(account, 0L) + amount);
    }
}