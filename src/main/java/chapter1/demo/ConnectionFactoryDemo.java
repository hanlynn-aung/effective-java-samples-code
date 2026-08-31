package chapter1.demo;

import chapter1.bad.BadConnectionFactory;
import chapter1.good.GoodCachedConnections;
import chapter1.good.GoodConnectionFactory;
import chapter1.good.GoodTypedConnections;

public final class ConnectionFactoryDemo {
    public static void main(String[] args) {
        System.out.println("== BadConnectionFactory (public constructor) ==");
        BadConnectionFactory bad = new BadConnectionFactory();
        BadConnectionFactory.Connection c1 = bad.open("db://x");
        BadConnectionFactory.Connection c2 = bad.open("db://x");
        System.out.println("null address accepted: "
                + (bad.open(null).address() == null));
        System.out.println("two opens are distinct instances: " + (c1 != c2));

        System.out.println();
        System.out.println("== GoodConnectionFactory (named static factory) ==");
        GoodConnectionFactory good = new GoodConnectionFactory();
        try {
            good.open(null);
            System.out.println("null NOT rejected (unexpected)");
        } catch (NullPointerException e) {
            System.out.println("null rejected immediately: " + e.getMessage());
        }

        System.out.println();
        System.out.println("== GoodCachedConnections (instance control) ==");
        var cs1 = GoodCachedConnections.to("db://x");
        var cs2 = GoodCachedConnections.to("db://x");
        System.out.println("same instance for same address: " + (cs1 == cs2));

        System.out.println();
        System.out.println("== GoodTypedConnections (returning subtypes) ==");
        GoodTypedConnections.Connection plain = GoodTypedConnections.plain("db://x");
        GoodTypedConnections.Connection secure = GoodTypedConnections.secure("https://x");
        System.out.println("plain connection class: " + plain.getClass().getSimpleName());
        System.out.println("secure connection class: " + secure.getClass().getSimpleName());
        System.out.println("secure connection reports secure: " + secure.secure());
    }
}