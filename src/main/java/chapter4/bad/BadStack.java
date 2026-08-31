package chapter4.bad;

import java.util.ArrayList;

public class BadStack extends ArrayList<String> {
    public String pop() {
        return remove(size() - 1);
    }
}
