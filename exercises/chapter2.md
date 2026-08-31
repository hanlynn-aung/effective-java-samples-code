# Chapter 2 — Exercises (Items 1–2)

Do these before reading the solutions.

## 1. Concept — three many-parameter patterns

For *each* of telescoping constructors, JavaBeans setters, and builders,
state (a) the defect it avoids or suffers, and (b) one real bug it can cause
in production.

## 2. Immutability — why the JavaBeans pattern can't win

Explain why an object built with the JavaBeans pattern can *never* be
immutable, and why a builder makes immutability natural. Point at the exact
mechanism in each case (`BadBeanPizza` vs `GoodBuilderUser`).

## 3. Decision — record vs builder vs constructor (Java 17)

For each of the following, choose: plain constructor, Java 17 `record`, or
builder — and justify in one sentence.

a) A `Point` with two `double` coordinates.
b) A `DatabaseConfig` with `host` (required), plus optional `port`, `poolSize`,
   `ssl`, `retryCount`, `readOnly`.
c) An `EventLogEntry` with a fixed `timestamp` and `message`, written once and
   shared everywhere.
d) A `PriceLevel` with required `ticker` and `price`, but prices change
   frequently and correctness must not rely on identity.

## 4. Rewrite — telescoping to builder

Convert to a builder with a required `host`, optional `port` (default 8080),
`secure` (default false), `maxConnections` (default 10), validated in
`build()` (port in 1..65535, maxConnections >= 1). Keep a private
constructor and a private nested `Builder`. The class is immutable.

## 5. Spot the defect — hierarchical generics

The fluent base-builder below is broken. What is missing, and what breaks at
compile time without it?

```java
public abstract class Drink {
    public abstract static class Builder<T extends Builder<T>> {
        protected abstract T self();
        public T sugar(int spoons) { return self(); }
        abstract Drink build();
    }
    public static class CoffeeBuilder extends Drink.Builder<CoffeeBuilder> {
        public CoffeeBuilder decaf() { return self(); }
        public Coffee build() { return new Coffee(); }
    }
}
```

## 6. Fix the call site — item 1 review

`GoodStaticFactoryService.connectedTo(null)` throws `NullPointerException`
at construction. Explain why failing *early* at the factory is better than
deferring the failure to first use, and name the mechanism
(`Objects.requireNonNull`) plus one more situation where fail-fast matters.

## 7. Singleton choice — item 3

A `MetricsRegistry` must be a singleton, and it is going to be (a)
serialized to disk periodically and (b) loaded by a bootstrap that uses
reflection. Choose between the enum idiom and a field + static factory, say
what each idiom guarantees, and identify where the field-based version must
add `readResolve()`. Which one do you ship?

## 8. Harden the utility class — item 4

```java
public final class Strings {
    public static String normalize(String s) {
        return s.trim().toLowerCase();
    }
}
```

What is wrong, and how do you make it noninstantiable and bulletproof even
against an accidental `new` inside the same class?

## 9. Refactor hardwiring to DI — item 5

```java
public final class InvoicingService {
    private final InvoiceStore store = new DbInvoiceStore();
    public String issue(String id) { return store.save(id); }
}
public final class DbInvoiceStore {
    public String save(String id) { return "db:" + id; }
}
```

Rewrite so the store is injected, then sketch the unit test that proves the
service really uses the injected fake.

## 10. Regex reuse + the balance — item 6

`"2000-01-01".matches("\\d{4}-\\d{2}-\\d{2}")` is called 50 000 times a
day in a filter. Fix it with a `Pattern`. Then explain the *other* direction
of item 6: give an example where reusing is WRONG and per-call objects are
fine.

## 11. Retained references — item 7

A message queue keeps dequeuing but the queue's backing array keeps its
maximum size and contents forever, and OOM appears after months. Explain the
mechanism, which field to null out and where, and name the profiler
technique a senior would use to find it in a heap dump. Also say where else
than arrays this bites (caches, listeners).

## 12. Finalizer replacement — item 8

```java
public class DatabaseHandle {
    protected void finalize() { disconnect(); }
    public void query(String sql) { /* uses native peer */ }
}
```

Why is this unreliable, and what is the replacement (interface, close
method, use guard, and the owning block)?

## 13. Try-with-resources — item 9

Given `read()` throws "read boom" and `close()` throws "close boom":

```java
String body;
try {
    body = reader.readAll();
} finally {
    reader.close();
}
```

What does the caller see with this code, and what does the caller see with
try-with-resources? Which method (`getSuppressed`) exposes the diff, and what
is the ordering rule when several resources are declared?