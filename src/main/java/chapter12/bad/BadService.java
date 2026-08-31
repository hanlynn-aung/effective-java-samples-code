package chapter12.bad;

import java.util.ArrayList;
import java.util.List;

public class BadService {
    public List<String> names = new ArrayList<>();

    public void add(String name) {
        names.add(name);
    }
}
