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
