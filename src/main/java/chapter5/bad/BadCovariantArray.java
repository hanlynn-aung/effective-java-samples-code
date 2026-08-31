package chapter5.bad;

public final class BadCovariantArray {

    public int firstElementOf(Object[] values) {
        return ((Number) values[0]).intValue();
    }

    public Object[] sneakyMixedBag() {
        Object[] bag = new Object[2];
        bag[0] = "one";
        bag[1] = 2;
        return bag;
    }

    public Object[] covariantTrap() {
        // String[] is assignable to Object[] because arrays are covariant...
        Object[] numbers = new Long[2];
        // ...which lets a String land in a Long[] and only explode on write.
        numbers[0] = "not a long";
        return numbers;
    }
}