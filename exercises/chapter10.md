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
