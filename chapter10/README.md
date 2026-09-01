# Chapter 10 — Exceptions (items 69–77)

Using exceptions the way the language intends: for exceptional conditions, at
the right abstraction, with the right type, well-documented and failure-atomic.
Round 1 covers items 69–73; round 2 covers 74–77.

---

## Item 69 — Use exceptions only for exceptional conditions

Exceptions are for **exceptional** conditions, not normal control flow. Using a
`catch` to end a loop or to branch on ordinary, expected outcomes is wrong, and
it's buggy: the code becomes slow, hard to read, and easy to get wrong (a
"terminating" exception can spin forever if the loop doesn't advance).

Rules of thumb:

- Prefer an explicit condition (`if`, a loop bound, `iterator.hasNext()`,
  `indexOf == -1`) for normal outcomes; exceptions are *not* a "return false".
- Exceptions should be rare enough that the just-in-time compiler can optimise
  the happy path believing exceptions won't happen.
- An exceptional condition is one the caller did not ask for and should
  generally not recover from by catching it as routine flow.

**Sample:** `BadExceptionControlFlow` uses `NoSuchElementException` to end a
search and `IndexOutOfBoundsException` to end a digit count — both are "just
got to the end of the loop" states being modelled as failures. The digit-count
version even risked an infinite loop because the `catch` was the only
termination. `GoodConditionCheck` uses a plain `for` with `value.length()` and
a normal `return false`. `ExceptionUseTest` proves the answers match, so the
"improvement" is purely the removal of control-flow-via-exception.

---

## Item 70 — Use checked exceptions for recoverable conditions and runtime
exceptions for programming errors

- **Checked exceptions** (`throws X`) are for conditions the caller can
  *realistically recover from* and therefore must handle — an insufficient
  balance, a full disk, a failed remote call. The compiler forces the client to
  confront them.
- **Runtime (unchecked) exceptions** are for **programming errors** and contract
  violations that should never happen if the caller follows the rules —
  `NullPointerException` (called with `null`), `IllegalArgumentException`
  (bad argument), `IllegalStateException` (called at the wrong time),
  `IndexOutOfBoundsException`. The fix is *in the caller's code*, so forcing a
  `catch` adds no recovery value.

Misdraught a programming error as checked and you saddle every caller with a
`try/catch` for something that's a bug to fix, not a situation to recover from.
Use unchecked exceptions freely for precondition violations and state bugs.

**Sample:** `BadCheckedForBug` makes `divide` (div by zero) and `setAge`
(invalid age) throw a checked `ProgrammingError` — these are programming errors.
`GoodCheckedRuntime.withdraw` throws a checked `InsufficientFundsException`
(the caller *can* do something: decline, retry, warn), while `divide` and
`setAge` throw unchecked `IllegalArgumentException`. `CheckedVsRuntimeTest`
confirms each type gets the exception category it warrants.

---

## Item 71 — Avoid unnecessary use of checked exceptions

A checked exception is a **demand** that the caller handle it. Overuse turns
every call site into a `try/catch` wall and makes an API a chore to use —
especially when the "failure" can't actually happen. Rules:

- Don't declare a checked exception on an operation that *cannot* meaningfully
  fail (a `get`/`put`/`size()` on an in-memory map can't throw a storage
  failure).
- If the only sensible reaction is to let the exception propagate or to crash,
  an unchecked exception (or no declared exception) is simpler.
- The book's test: *does the caller recover?* If it can't meaningfully recover,
  don't force it.
- When one truly must fail, prefer a **single method that throws checked once**
  at the boundary rather than sprinkling `throws` on every internal helper.

**Sample:** `BadOverChecked` declares a `StorageException` on `put`, `get`,
`contains`, and `size()` — four catch-happy accessors over a plain map where
`size()` *cannot* fail. `GoodOptionalChecked` drops the declaration entirely;
callers just use the map. `CheckedOveruseTest` shows the bad version working
only under `throws StorageException` while the good version needs none.

---

## Item 72 — Favor the use of standard exceptions

Reusing the JDK's standard exception types means callers already know how to
catch, reason about, and recover from your failures — no documentation needed,
consistent semantics. Prefer (in this order) the common ones:

- `IllegalArgumentException` — a bogus argument.
- `IllegalStateException` — called when the object is in the wrong state.
- `NullPointerException` — a `null` where non-null was required.
- `IndexOutOfBoundsException` / `IndexOutOfBoundsException` subclasses.
- `ConcurrentModificationException` — concurrent mutation detected.
- `UnsupportedOperationException` — method not implemented/unsupported.
- `NoSuchElementException` — a query found nothing.

Don't invent a custom exception for a case a standard one already expresses —
especially when the semantic is *exactly* a standard exception. Invent one only
for a genuinely distinct, caller-typed failure with its own recovery meaning.

**Sample:** `BadInventedException` wraps a null/empty check in a bespoke
checked `MyException`. `GoodStandardException` throws `NullPointerException`
for `null` and `IllegalArgumentException` for empty — standard, catchable by
every Java programmer. `StandardExceptionTest` pins both.

---

## Item 73 — Throw exceptions appropriate to the abstraction

When a low-level component fails, a high-level method should **translate** the
low-level exception into one that makes sense at *its* level of abstraction.
Leaking a `SQLException` (or a raw `IOException`, `SocketException`) out of a
`loadConfig()` betrays internal implementation details to callers who neither
know nor care about SQL — and it collapses your design's seam (item 65).

Fix by **exception translation**: catch the low-level exception and throw a
higher-level one, **chaining the cause** so the original trace survives:

```java
catch (SQLException e) {
    throw new ConfigLoaderException("failed loading config for " + key, e);
}
```

Chain the cause (`Throwable` constructor) so debugging still reaches the
root. Translate to the abstraction's own exception type, or to a standard one
(item 72) that matches the abstraction. Translation is worth it whenever the
lower-level failure's type is meaningless or misleading at the higher layer.

**Sample:** `BadLeakyException.loadConfig` catches `SQLException` and rethrows
it wrapped in a bare `RuntimeException` — the SQL abstraction leaks out of a
config loader. `GoodTranslation` throws a purpose-built `ConfigLoaderException`
whose `cause` is the original `SQLException`. `ExceptionTranslationTest`
verifies the good version keeps the cause chain intact while reporting at the
right level.

---

## Senior checklist

- [ ] Exceptions only for exceptional conditions; normal outcomes via conditions (69).
- [ ] Checked = recoverable; unchecked = programming errors (70).
- [ ] No forced `catch` on calls that can't meaningfully fail (71).
- [ ] Favor standard exceptions over invented ones (72).
- [ ] Translate low-level exceptions to the abstraction, chaining the cause (73).

---

*Round 2 (74–77: document exceptions, failure-capture details, failure
atomicity, never ignore exceptions) continues.*
