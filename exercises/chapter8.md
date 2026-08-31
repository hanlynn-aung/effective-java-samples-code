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
