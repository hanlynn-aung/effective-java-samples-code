# Chapter 8 — Exercises (items 49–51)

## Exercise 1 — Validate it (item 49)

```java
public void transfer(Account to, double amount) {
    balance -= amount;
    to.balance += amount;
}
```

List every parameter the method silently accepts that it shouldn't (types,
ranges, and finiteness), then rewrite it so each bad input fails fast with a
specific exception, without ever moving money. Which checks belong in a
constructor vs. per-method?

## Exercise 2 — Plug the leak (item 50)

```java
public final class Workbook {
    private final List<Row> rows;
    public Workbook(List<Row> rows) { this.rows = rows; }
    public List<Row> getRows() { return rows; }
}
```

`Row` is mutable and callers hold the `List` they passed in. Identify the two
leaks and fix both with defensive copies, then say what would eliminate the
need for copies entirely.

## Exercise 3 — De-flag the API (item 51)

```java
// schedule(what, whenEpochMillis, retryOnFail, notifyUser, sendSms, failHard)
```

Turn this six-parameter call with three booleans into something a reviewer can
read at a glance. Justify each change you make (naming, an enum, and/or a
builder) and show the rewritten call site.

## Exercise 4 — Tame the overloads (item 52)

```java
public void send(String msg) { ... }
public void send(Bytes bytes) { ... }
public void send(EncodedBytes bytes) { ... }
```

Explain, with one concrete example, how a caller could hit the *wrong* overload
at compile time when the runtime type differs from the static type. Then show
how renamed methods remove the ambiguity.

## Exercise 5 — Harden the varargs (item 53)

```java
public static int max(int... values) { ... }   // reads values[0]
```

Calling `max()` compiles but crashes at runtime. Rewrite the signature so an
empty call cannot compile, keep the variadic convenience for multiple values,
and say when you would *avoid* `T...` for performance.

## Exercise 6 — Empty, never null (item 54)

A DAO method is documented "returns all rows or nothing, if none". It currently
returns `null` for an empty result. Rewrite it to return an empty collection,
and list three caller idioms that were NPE-risky before and are now safe.

## Exercise 7 — Optional right-sizing (item 55)

```java
public Optional<List<Price>> quotes() { ... }
public Optional<Integer> bestRank() { ... }
```

Critique both signatures. Rewrite each the idiomatic way and say which of
`orElse`, `orElseGet`, `orElseThrow`, and `ifPresent` you'd use for each consumer
scenario.

## Exercise 8 — Document the contract (item 56)

```java
public double monthlyFee(double balance) { ... }
```

Write a Javadoc that states the input preconditions (non-NaN, non-negative),
the return value (a percentage of `balance`, unrounded), and the one
`IllegalArgumentException` case — using `@param`, `@return`, `@throws`,
`@implSpec`, and `{@code}`/`{@link}`. Then note one way to *verify* the doc and
code agree.
