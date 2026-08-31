package chapter8.good;

import java.util.Objects;

public final class GoodSignature {

    public enum Requirement {
        ACTIVE, VERIFIED, RECENT
    }

    public boolean qualifies(String name, String region, Requirement[] mustHold) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(region, "region");
        for (Requirement requirement : mustHold) {
            if (!holds(requirement)) {
                return false;
            }
        }
        return true;
    }

    private boolean holds(Requirement requirement) {
        return switch (requirement) {
            case ACTIVE, VERIFIED, RECENT -> true;
        };
    }
}