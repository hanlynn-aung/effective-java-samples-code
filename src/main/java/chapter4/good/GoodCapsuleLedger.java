package chapter4.good;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GoodCapsuleLedger {
    private final Map<String, Long> balances;
    private long total;

    private GoodCapsuleLedger(Map<String, Long> balances, long total) {
        this.balances = balances;
        this.total = total;
    }

    public static GoodCapsuleLedger create() {
        return new GoodCapsuleLedger(new LinkedHashMap<>(), 0L);
    }

    public void record(String account, long amount) {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("account must not be blank");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
        long updated = balances.getOrDefault(account, 0L) + amount;
        balances.put(account, updated);
        total += amount;
    }

    public long balanceOf(String account) {
        return balances.getOrDefault(account, 0L);
    }

    public long total() {
        return total;
    }
}