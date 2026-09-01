# Chapter 10 — Exercises (items 69–73)

## Exercise 1 — Not control flow (item 69)

```java
try {
    while (true) {
        consume(queue.remove());       // NoSuchElementException ends the loop
    }
} catch (NoSuchElementException e) { /* loop finished */ }
```

Explain two concrete problems with terminating a loop via an exception, then
rewrite with explicit conditions (`!queue.isEmpty()` / `poll()` / `hasNext()`)
and no exception for the normal path.

## Exercise 2 — Checked or runtime? (item 70)

Classify each as **checked** (recoverable) or **unchecked** (programming
error), and say who's at fault / what recovery looks like:

1. A `withdraw()` where the balance is insufficient.
2. `parseInt("12x")` on a genuinely malformed user string.
3. Calling `iterator().next()` past the end of an iterator.
4. `openFile("/nonexistent")` for a path a user typed.
5. `setAge(-5)`.

## Exercise 3 — Too many checked (item 71)

```java
public Balance loadBalance(String acct) throws AccountException { ... }
public void update(Balance b) throws AccountException { ... }
public Balance snapshot() throws AccountException { ... }
public long version() throws AccountException { ... }
```

One of these can legitimately throw a checked exception; three are over-burdened.
Which is which, and how should the other three be declared?

## Exercise 4 — Standard exceptions (item 72)

For each failure, pick the most standard JDK exception:

1. A method whose argument list is `null`.
2. Adding an element to an unmodifiable collection throws…?
3. A pop from an empty stack (semantically "no element").
4. Calling `start()` twice on an already-started service.
5. An array index that is out of bounds.

## Exercise 5 — Translate the cause (item 73)

```java
public void save(Path p) throws IOException {
    fileOps.write(p);      // throws a low-level IOException with a raw message
}
```

This `save` leaks the low-level layer. Rewrite it to a higher-level
`PersistenceException` that (a) makes sense to a caller of `save`, (b) chains
the original `IOException` as the cause, and (c) includes the `Path` in the
message. Why is chaining the cause non-negotiable?

## Exercise 6 — Document the throws (item 74)

```java
public double total(String currency) {
    if (currency == null) throw new NullPointerException("currency");
    if (!SUPPORTED.contains(currency)) throw new IllegalArgumentException("unsupported: " + currency);
    if (!ratesReady) throw new IllegalStateException("rates not loaded");
    return compute(currency);
}
```

Write the Javadoc for `total` declaring every thrown (unchecked) exception with
its trigger, plus the `@param`/`@return`. Then state the one rule that links
this doc to the code so they can't drift.

## Exercise 7 — Capture the failure (item 75)

```java
try { write(record); } catch (IOException e) { throw new RuntimeException("failed"); }
```

The message `"failed"` captures nothing. Rewrite so the message includes the
record key and the path, and the new exception chains the original `IOException`
as its cause.

## Exercise 8 — Make it atomic (item 76)

```java
void transfer(Account from, Account to, Money amount) {
    from.debit(amount);
    to.credit(amount);      // if this throws, `from` is already debited
}
```

`to.credit` can throw (e.g. daily-limit). The method is currently non-atomic.
Rewrite it failure-atomic, and explain which of the four strategies (validate
first / copy-swap / restore / order-by-commit) your fix uses.

## Exercise 9 — Don't ignore (item 77)

A reviewer finds:

```java
try { cache.rebuild(); } catch (Exception ignore) { /* no-op */ }
```

Explain why the empty catch is a defect, give three acceptable alternatives
(rethrow / wrap / log-with-context), and describe the single legitimate case
where ignoring might be defensible — plus what the code must still do there.
