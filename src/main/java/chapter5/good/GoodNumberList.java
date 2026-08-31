package chapter5.good;

import java.util.ArrayList;
import java.util.List;

public final class GoodNumberList {

    public List<Number> mixedNumbers() {
        List<Number> numbers = new ArrayList<>();
        numbers.add(Integer.valueOf(42));
        numbers.add(Double.valueOf(3.14));
        return numbers;
    }
}