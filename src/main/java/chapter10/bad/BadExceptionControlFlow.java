package chapter10.bad;

import java.util.Iterator;
import java.util.List;

public final class BadExceptionControlFlow {

    public boolean contains(List<String> values, String target) {
        Iterator<String> it = values.iterator();
        try {
            while (true) {
                String next = it.next();
                if (next.equals(target)) {
                    return true;
                }
            }
        } catch (java.util.NoSuchElementException e) {
            return false;
        }
    }

    public int countDigits(String value) {
        int count = 0;
        int pos = 0;
        try {
            while (true) {
                count += Character.isDigit(value.charAt(pos)) ? 1 : 0;
                pos++;
            }
        } catch (IndexOutOfBoundsException e) {
            return count;
        }
    }
}