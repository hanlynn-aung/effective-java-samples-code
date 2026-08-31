package chapter9.good;

import java.util.Map;

public final class GoodPrimitives {

    public long sum(long a, long b) {
        return a + b;
    }

    public boolean sameRank(long a, long b) {
        return a == b;
    }

    public int maxUnboxed(Map<String, Integer> map) {
        int best = Integer.MIN_VALUE;
        for (int value : map.values()) {
            if (value > best) {
                best = value;
            }
        }
        return best;
    }

    public int countAbove(Map<String, Integer> map, int threshold) {
        int count = 0;
        for (int value : map.values()) {
            if (value > threshold) {
                count++;
            }
        }
        return count;
    }

    public int compare(long a, long b) {
        return Long.compare(a, b);
    }
}