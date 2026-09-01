# Chapter 10 — Exceptions (items 69–77)

Using exceptions the way the language intends: for exceptional conditions, at
the right abstraction, with the right type, well-documented and failure-atomic.
All nine items (69–77) are built across two rounds.

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

## Item 74 — Document all exceptions thrown by each method

An API's contract isn't just "what it returns" — it's **what it throws and
when**. Every method should document, in its Javadoc, every checked exception
(the compiler already forces declaration) **and every meaningful unchecked
exception** a caller might hit. Use `@throws` with the condition:

- `@throws NullPointerException if x is null`
- `@throws IllegalArgumentException if y is negative`
- `@throws IllegalStateException if the object is not initialised`

This is part of item 56's discipline: doc and code must agree. Undocumented
exceptions surprise callers who can't know what to guard against; a
documented-but-wrong `@throws` is a lie that a test should catch. Inherited
methods can refine the contract with `@throws` on the subclass.

**Sample:** `BadUndocumented.twoDigitYear` throws `IllegalArgumentException`
for a negative year with no doc comment at all. `GoodDocExceptions` documents
the `@param` precondition, `@return`, the `@throws` condition, and an
`@apiNote` pointing to `java.time.Year`. `ExceptionDocTest` shows the documented
contract (negative year → `IllegalArgumentException`) is actually honoured.

---

## Item 75 — Include failure-capture information in detail messages

When an exception is thrown the detail message is the first — often only —
evidence a developer sees. It should let you **reconstruct the failure without
guessing**: "what was asked, what was expected, what went wrong." A message of
`"invalid"` or `"bad index"` is a dead end; it names nothing.

Good messages capture the offending values and context:

```java
throw new IndexOutOfBoundsException("index " + i + " out of bounds for length " + size);
throw new IllegalArgumentException("balance " + b + " < amount " + a);
```

Don't vary the message or embed HTML/sensitive data as decoration — keep it
clean, specific, and reproducible. (The *type* of the exception is decided by
item 72; the *message* is decided here.)

**Sample:** `BadVagueMessage` says `"invalid"` and `"bad index"`.
`GoodDetailMessage` says `"index 9 out of bounds for length 3"` and `"divide(10,
0)"`. `DetailMessageTest` checks the good messages actually contain the
reproduction values (the index, the length, the operands).

---

## Item 76 — Strive for failure atomicity

A failed method invocation should leave the object **in the state it was in
before the call** — that's *failure atomicity*. If a call throws, the object
shouldn't be half-way through a mutation, corrupted or partially written.
Strategies, in order of preference:

1. **Validate first, then mutate** — check all preconditions (including
   `null`s, ranges, returned value validity) before touching any state.
2. **Make a copy, then swap** — work on a temporary/local structure and commit
   it at the end (`items.addAll(validated)`).
3. **Restore in a `catch`** — if a check must happen mid-mutation, undo the
   partial writes before rethrowing (more error-prone; prefer 1 or 2).
4. Unchecked exceptions, checked exceptions, and errors should all aim for
   atomicity — especially for objects used elsewhere during the call.

**Sample:** `BadNonAtomic.addBatch` appends each item as it goes, then throws on
a `null` — the demo shows a failed batch leaving 2 items committed (a partial
write). `GoodAtomic.addBatch` **validates every item first**, then
`addAll`s — a failed call leaves the list unchanged. `FailureAtomicityTest`
pins the bad partial state (2) vs. the good unchanged state (0).

---

## Item 77 — Don't ignore exceptions

An empty `catch {}` (`// ignore`, `catch (Exception e) {}`) is one of the worst
things you can do: it throws away the very information that diagnosis depends
on, leaving a black box that "sometimes silently misbehaves." If you catch an
exception, do *something*:

- **Rethrow** if you can't act (`throw e;`).
- **Wrap with context** (item 73 / 75) to rethrow a more useful exception.
- **Log it with context** if it's genuinely non-fatal and you must continue.
- **Act deliberately** — if you truly decide to ignore it, make that decision
  explicit and visible (a named, documented branch), never a blank catch.

The one honourable exception: an intervening lower-level framework that must
rethrow; and a *documented* "best-effort" policy — but even then, record
something, at least at debug level. *Never let the failure disappear silently.*

**Sample:** `BadSwallowed` has an empty `catch (RuntimeException ignored)`, a
`safeParse` that swallows `NumberFormatException` into a `-1` the caller can't
distinguish from a real value, and a silent-log catch. `GoodHandle` routes
exceptions to the uncaught-exception handler, wraps a parse failure with the
offending input, and validates with `requireNonNull` so a `null` row fails
loudly. `IgnoreExceptionTest + the demo show the swallowed `-1` versus a
surfaced, contextual failure.

---

## Senior checklist

- [ ] Exceptions only for exceptional conditions; normal outcomes via conditions (69).
- [ ] Checked = recoverable; unchecked = programming errors (70).
- [ ] No forced `catch` on calls that can't meaningfully fail (71).
- [ ] Favor standard exceptions over invented ones (72).
- [ ] Translate low-level exceptions to the abstraction, chaining the cause (73).
- [ ] Every method's Javadoc names all thrown exceptions (`@throws`), checked and
      meaningful unchecked (74).
- [ ] Detail messages capture the values needed to reproduce the failure (75).
- [ ] A method that fails leaves the object in its prior state where possible (76).
- [ ] Never swallow an exception silently — rethrow, wrap with context, or act
      deliberately and visibly (77).
