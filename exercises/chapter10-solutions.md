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

## Solution 6 — Document the throws (item 74)

```java
/**
 * Returns the total for a supported currency.
 *
 * @param currency the ISO currency code, which must be non-null and supported
 * @return the running total for that currency
 * @throws NullPointerException     if {@code currency} is null
 * @throws IllegalArgumentException if {@code currency} is not supported
 * @throws IllegalStateException    if exchange rates are not loaded yet
 */
```

The one rule linking doc to code: **the `@throws` is a contract the tests
enforce** — write a unit test that asserts `total(null)`, `total("ZZZ")`, and
calling before `loadRates()` throw exactly the documented types. A passing test
is the proof the documentation and the implementation can't drift apart.

## Solution 7 — Capture the failure (item 75)

```java
try {
    write(record);
} catch (IOException e) {
    throw new RuntimeException("failed writing record " + record.key()
            + " to " + destinationPath, e);
}
```

The message now names the record key and the path (reproduction values), and
the `new RuntimeException(msg, e)` chains the original `IOException` as the
cause so the root stack survives. Without those two things the log line says
only "failed" — undiagnosable.

## Solution 8 — Make it atomic (item 76)

```java
void transfer(Account from, Account to, Money amount) {
    // strategy 1: validate first - check the debit AND the credit both succeed
    // before mutating either account (e.g. check both limits up front).
    from.validateDebit(amount);
    to.validateCredit(amount);
    from.debit(amount);
    to.credit(amount);   // now cannot throw for a limit
}
```

The fix uses **"validate first, then mutate"**: by checking both sides' limits
*before* any mutation, `to.credit` can no longer fail from a limit, so `from`
never ends up debited alone. (Where a mid-step failure is still possible, switch
to *copy-swap* — compute on temporaries and commit both at the end — or *restore*
in a `catch`. Whatever the strategy, a failed `transfer` must leave both
accounts unchanged.)

## Solution 9 — Don't ignore (item 77)

The empty `catch (Exception ignore)` is a defect because:
- it **destroys diagnosis** — the exception (type, message, trace) is gone;
- it **hides failure** — `cache.rebuild()` may be silently broken and every
  later read serves stale/empty data with no signal;
- it **catches everything**, including programming errors (`Exception` is too
  broad), turning real bugs into silent no-ops.

Acceptable alternatives:

```java
catch (Exception e) { throw e; }                      // 1. rethrow if you can't act
catch (RebuildException e) { throw new CacheException("rebuild failed", e); } // 2. wrap
catch (RebuildException e) { log.error("cache rebuild failed", e); }          // 3. log+continue
```

The one defensible case: a **best-effort, explicitly-documented** step where
failure is an acceptable outcome (e.g. writing a telemetry "last-seen" marker on
a crash path). Even there the code must still **log at least the exception** as
context, and the decision must be named — never a blank `catch {}` with no
trace at any level.
