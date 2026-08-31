package chapter9.bad;

import java.util.List;

public final class BadWideScope {

    private int total;

    public int sum(List<Integer> values) {
        total = 0;
        int i = 0;
        while (i < values.size()) {
            total += values.get(i);
            i++;
        }
        int loopCount = i;
        return total + loopCount - loopCount;
    }

    public boolean containsPositive(List<Integer> values) {
        int idx = 0;
        while (idx < values.size()) {
            if (values.get(idx) > 0) {
                return true;
            }
            idx++;
        }
        return false;
    }
}