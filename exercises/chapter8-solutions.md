# Chapter 8 — Solutions (items 49–51)

## Solution 1 — Validate it (item 49)

Silently accepted:

- `to == null` → must throw `NullPointerException`.
- `amount` is `NaN` → the comparison checks below all pass for `NaN` (every
  comparison with `NaN` is false), so it must be rejected **explicitly**.
- `amount <= 0` → a transfer of zero/negative that moves money is almost always
  a bug → `IllegalArgumentException`.
- `amount > balance` → overdraft (if not allowed) → `IllegalArgumentException`
  or a checked business exception.

```java
public void transfer(Account to, double amount) {
    Objects.requireNonNull(to, "to");
    if (Double.isNaN(amount) || amount <= 0) {
        throw new IllegalArgumentException("amount must be positive: " + amount);
    }
    if (amount > balance) {
        throw new IllegalArgumentException("insufficient balance: " + balance);
    }
    balance -= amount;
    to.balance += amount;
}
```

**Constructor vs. method:** structural invariants that must hold for the
object's whole life (e.g. a non-negative `initialBalance`) belong in the
constructor so the object isn't born broken; operation-specific inputs
(`amount` for this one transfer, the target account) belong in the method. The
order matters: **validate everything before mutating anything**, so a failed
call never leaves money half-moved.

## Solution 2 — Plug the leak (item 50)

Two leaks:

1. **Out** — `getRows()` returns the internal mutable list; a caller can
   `getRows().add(evil)`.
2. **In/alias** — the constructor stores the caller's `List` by reference;
   the caller's later `rows.add(...)` mutates the workbook.

```java
public final class Workbook {
    private final List<Row> rows;
    public Workbook(List<Row> rows) {
        this.rows = List.copyOf(rows);          // trusted copy, immutable
    }
    public List<Row> getRows() {
        return rows;                            // safe: it IS immutable now
    }
}
```

`List.copyOf` both copies *and* makes the result immutable, so the getter needs
no further copy and callers can't mutate it. The need for defensive copying
vanishes entirely the moment the **types** are immutable — if `Row` were an
immutable value and the collection an unmodifiable view, there'd be nothing to
corrupt.

## Solution 3 — De-flag the API (item 51)

```java
public enum Notification { NONE, EMAIL, SMS, EMAIL_AND_SMS }

public void schedule(Job what,
                     Instant when,              // already typed; Instant not raw millis
                     boolean retryOnFailure,    // fine as one named boolean
                     Notification notify) {     // enum replaces two booleans
    ...
}
```

Changes and the reasoning:

- `whenEpochMillis` → `Instant when`: a named, type-safe time rather than a raw
  `long` that could be seconds or millis.
- `notifyUser, sendSms` (two overlapping booleans — what about email+SMS, or
  none?) → `Notification` **enum**: mutually exclusive, self-documenting, and
  extensible without breaking the signature.
- `failHard` → dropped/renamed into the method's contract (fail-closed is the
  safe default; expose it only if callers genuinely need the choice).
- For more than four parameters, the same method could take an immutable
  `ScheduleRequest` built via a `ScheduleRequest.builder()`, giving per-field
  names and defaults.

Call site now reads correctly:

```java
scheduler.schedule(job, Instant.now().plus(5, MINUTES), true, Notification.EMAIL);
```

No boolean soup, no positional guessing, and the compiler rejects
`Notification.SMS` typos and wrong time units.

## Solution 4 — Tame the overloads (item 52)

Overload resolution is by **static type at compile time**, never runtime type.
A caller holding an actual `EncodedBytes` in a variable typed as the supertype
`Bytes` will invoke `send(Bytes)`, not `send(EncodedBytes)` — so if the caller
wants the encoded path, they silently get the wrong one:

```java
Bytes b = new EncodedBytes(...);
send(b);                  // hits send(Bytes), not the intended send(EncodedBytes)
```

Renaming removes it because there's no shared selector to resolve:

```java
sendPlain(Encoded<...>...)   // distinct names -> the name says what runs
```

Rename so each method's name carries its variant (`sendPlain`, `sendEncoded`,
`sendBytes`), or give them genuinely different parameter types so static type
can't be ambiguous. This is also why you should never overload two methods that
differ only so a `Collection` variable silently picks one branch.

## Solution 5 — Harden the varargs (item 53)

Give the first (required) value its own parameter:

```java
public static int max(int first, int... rest) {
    int max = first;
    for (int value : rest) {
        if (value > max) max = value;
    }
    return max;
}
```

Now `max()` doesn't compile — "at least one" is enforced structurally — while
`max(7)`, `max(7, 3, 9)` still work. **When to avoid `T...`:** array allocation
per call means a hot loop like `strList.concat("x")` in a loop allocates a new
array every iteration; there, prefer an explicit `String` overload or a
single-arg `concat(String)` plus a separate `concatAll(String...)`. Reserve
`T...` for genuinely variadic, low-frequency convenience calls.

## Solution 6 — Empty, never null (item 54)

```java
public List<Row> getAllRows(String table) {
    return rowsFor(table);          // List.copyOf/List.of when empty
}
```

Returning `List.of()` for the empty case makes these formerly-NPE-risky idioms
safe:

1. `for (Row r : dao.getAllRows("x")) { ... }` — no null-check, no NPE.
2. `dao.getAllRows("x").size()` — returns `0`, doesn't throw.
3. `dao.getAllRows("x").stream().count()` / `...getAllRows("x").isEmpty()` —
   stream and collection APIs work on the empty value.

The contract becomes "empty list means no results", which needs no prose and no
defensive `if (rows != null)` at every caller.

## Solution 7 — Optional right-sizing (item 55)

Critique:

- `Optional<List<Price>>` is a **collection wrapped in Optional** — pointless.
  An empty `List` already encodes "no quotes". Also costs an extra allocation
  per call.
- `Optional<Integer>` **boxes a primitive** just to carry an absence flag;
  use `OptionalInt`.

```java
public List<Price> quotes() { ... }          // plain, possibly-empty collection
public OptionalInt bestRank() { ... }        // primitive Optional for an absent rank

// consumers
List<Price> q = quotes();                      // no Optional ceremony at all
int r = bestRank().orElse(0);                  // eager default
int r2 = bestRank().orElseGet(() -> compute()); // lazy (only called when absent)
int r3 = bestRank().orElseThrow(() -> new NotFoundException("no rank"));
bestRank().ifPresent(rank -> cache.put(rank));   // act only when present
```

`OptionalInt` avoids the `Integer` box; `quotes()` never saw an `Optional` at
all.

## Solution 8 — Document the contract (item 56)

```java
/**
 * Computes the monthly maintenance fee for a balance.
 *
 * @param balance the account balance in minor units; must be non-negative and
 *                not {@link Double#NaN NaN}
 * @return 0.5% of {@code balance}, <em>not</em> rounded to the cent
 * @throws IllegalArgumentException if {@code balance} is negative or NaN
 * @implSpec implementations must use {@code balance * 0.005} and not round;
 *           callers requiring exact money must round explicitly
 */
```

To verify doc and code agree: write a unit test that **asserts the documented
contract** — call `monthlyFee(-1)` and `monthlyFee(Double.NaN)` and assert
`IllegalArgumentException`, and call `monthlyFee(10_000)` and assert the
unrounded `50.0`. A passing test is the proof that the doc comment is not a lie.
