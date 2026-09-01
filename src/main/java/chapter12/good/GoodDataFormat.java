package chapter12.good;

import java.util.Objects;

/**
 * A stable, human-readable interchange format - the recommended alternative to
 * raw Java serialization. The bytes are explicit and versioned, so a reader for
 * "v1" can keep reading old records even as the model evolves, and there is no
 * magic object-graph deserialization (immune to gadget injection).
 */
public final class GoodDataFormat {

    private static final String VERSION = "v1";

    private final String name;
    private final int age;

    public GoodDataFormat(String name, int age) {
        this.name = Objects.requireNonNull(name);
        this.age = age;
    }

    public static GoodDataFormat parse(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4 || !VERSION.equals(parts[0]) || !"GoodDataFormat".equals(parts[1])) {
            throw new IllegalArgumentException("unsupported record: " + line);
        }
        return new GoodDataFormat(parts[2], Integer.parseInt(parts[3]));
    }

    public String toRecord() {
        return VERSION + "|GoodDataFormat|" + name + "|" + age;
    }

    public String name() {
        return name;
    }

    public int age() {
        return age;
    }
}