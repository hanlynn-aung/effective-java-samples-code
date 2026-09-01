package chapter9.good;

public final class GoodInterfaceInvocation {

    private final Greeter greeter;

    public GoodInterfaceInvocation(Greeter greeter) {
        this.greeter = greeter;
    }

    public String buildGreeting(String name) {
        return greeter.greet(name);
    }

    public static final class FriendlyGreeter implements Greeter {
        @Override
        public String greet(String name) {
            return "Hello, " + name + "!";
        }
    }
}