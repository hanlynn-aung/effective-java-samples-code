# Chapter 1 — Exercises

Do these before reading the solutions. Aim to answer from memory.

## 1. Concept — the four benefits

Name the four benefits of a static factory method over a public constructor,
and give a one-line real-world example for each.

## 2. Concept — instance control

`GoodCachedConnections.to("db://x")` called twice returns the *same* instance
(Tests: `cachedReturnsSameInstance`).

a) What property must a class have before you may cache and share instances?
b) Why is that the case — what goes wrong if you cache a mutable object?

## 3. Concept — returning subtypes

`GoodTypedConnections.plain(...)` and `secure(...)` return different private
classes behind the `Connection` interface.

a) Why can a public constructor never give you this freedom?
b) What does a *service-provider framework* (e.g. `ServiceLoader`, JDBC
   `DriverManager`) build on top of this benefit?

## 4. Reasoning — when NOT to use a factory

Give two situations where a plain public constructor is the better choice.

## 5. Fix the code — rewrite with a static factory

The class below has no validation and a second public constructor to express
a "temporary order". Rewrite it using two named static factories, a private
constructor, and `Objects.requireNonNull`. Keep it immutable (final class,
final fields).

```java
public final class Order {
    public final String customer;
    public final boolean temporary;

    public Order(String customer) { this(customer, false); }
    public Order(String customer, boolean temporary) {
        this.customer = customer;
        this.temporary = temporary;
    }
}
```

## 6. Spot the trap — boxing identity

Why does `Integer.valueOf(127) == Integer.valueOf(127)` print `true` while
`Integer.valueOf(128) == Integer.valueOf(128)` prints `false`? How should
you *actually* compare two `Integer` values, and why does the cache prevent
nothing being safe about `==`?