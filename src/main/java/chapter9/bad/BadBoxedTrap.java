package chapter9.bad;

import java.util.Map;

public final class BadBoxedTrap {

    public Integer sum(Integer a, Integer b) {
        return a + b;
    }

    public boolean sameRank(Integer a, Integer b) {
        return a == b;
    }

    public int maxUnboxed(Map<String, Integer> map) {
        Integer best = null;
        for (Integer value : map.values()) {
            if (best == null || value > best) {
                best = value;
            }
        }
        return best;
    }

    public int countAbove(Map<String, Integer> map, int threshold) {
        int count = 0;
        for (Integer value : map.values()) {
            if (value > threshold) {
                count++;
            }
        }
        return count;
    }
}