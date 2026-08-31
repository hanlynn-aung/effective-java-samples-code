package chapter11.good;

import java.io.Serial;
import java.io.Serializable;

public final class GoodUserData implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private final String name;
    private transient final String password;

    public GoodUserData(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String name() { return name; }
    public String password() { return password; }
}
