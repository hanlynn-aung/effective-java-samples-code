# Chapter 10 — Solutions (items 69–73)

## Solution 1 — Not control flow (item 69)

Problems:

1. **Performance & JIT** — an exception thrown thousands of times per loop
   defeats the happy-path optimisation; exception construction captures a
   stack trace each time.
2. **Correctness/fragility** — the "end of loop" is modelled as a failure, so
   any *genuine* `NoSuchElementException` is indistinguishable from normal
   completion and gets silently swallowed; and if the loop body forgets to
   advance, it spins forever (the earlier demo version risked exactly this).

```java
while (!queue.isEmpty()) {
    consume(queue.poll());     // explicit, no exception for the normal path
}
// or with an iterator:
for (Item item = queue.poll(); item != null; item = queue.poll()) { consume(item); }
```

Here the condition is the normal "anything left?" check — the loop terminates
cleanly and no exception is ever thrown on the happy path.

## Solution 2 — Checked or runtime? (item 70)

1. **`withdraw` insufficient balance → CHECKED.** Recoverable: the caller can
   decline the transaction, warn the user, try another account. The caller
   genuinely has something to do, so it must handle it.
2. **`parseInt("12x")` on malformed user input → commonly unchecked**
   (`NumberFormatException`): callers validate/parse defensively; the fix is in
   validation, and `NumberFormatException` is already a runtime exception.
3. **`iterator().next()` past the end → unchecked** (`NoSuchElementException`):
   a programming error — the caller violated `hasNext()`. Fix the caller.
4. **`openFile("/nonexistent")` for a user-typed path → CHECKED**
   (`IOException`/`FileNotFoundException`): recoverable — the caller can show an
   error, prompt again, or fall back. The user's input is external, not a bug.
5. **`setAge(-5)` → unchecked** (`IllegalArgumentException`): the caller passed
   a bad argument — a programming error; fix in the caller.

So: 1 and 4 are checked (recoverable, caller must handle); 2, 3, 5 are
unchecked (programming/validation errors).

## Solution 3 — Too many checked (item 71)

`loadBalance(acct)` legitimately throws a checked `AccountException`: reading
from an external account store can fail (offline store, missing account) and
the caller can recover (retry, report). The other three are over-burdened:

- `update(b)` — an in-memory mutation that can't inherently fail to "update";
  drop the `throws`.
- `snapshot()` — an in-memory read; drop the `throws`.
- `version()` — cannot fail at all; drop the `throws`.

Rewrite them as `public void update(Balance b)`, `public Balance snapshot()`,
`public long version()` — no `throws`. Keep the checked exception pinned to the
operation that genuinely crosses a fallible boundary.

## Solution 4 — Standard exceptions (item 72)

1. `null` argument list → **`NullPointerException`**.
2. Adding to an unmodifiable collection → **`UnsupportedOperationException`**.
3. Pop from an empty stack → **`NoSuchElementException`** (that's exactly what
   the iterator/stack protocols throw for "no element").
4. `start()` twice on an already-started service → **`IllegalStateException`**
   (called in the wrong state).
5. Out-of-bounds array index → **`IndexOutOfBoundsException`** (or its
   `ArrayIndexOutOfBoundsException` subtype).

These are standard precisely so every caller already understands them without
reading a custom exception's prose.

## Solution 5 — Translate the cause (item 73)

```java
public static final class PersistenceException extends Exception {
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}

public void save(Path p) throws PersistenceException {
    try {
        fileOps.write(p);
    } catch (IOException e) {
        throw new PersistenceException("failed to save " + p.toAbsolutePath(), e);
    }
}
```

- The exception now speaks the caller's language ("save failed at this path"),
  not the file-layer's raw message.
- `super(message, cause)` **chains the original `IOException`**, so the full
  stack trace and root cause survive into logs.
- The `Path` in the message gives the failure-capture detail (item 75) a
  reader needs to reproduce the problem.

Chaining is non-negotiable because without the `cause`, the original
lower-level failure (and its stack trace, message, and suppressed exceptions) is
lost — a translated exception that drops its cause makes real production bugs
un-debuggable.
