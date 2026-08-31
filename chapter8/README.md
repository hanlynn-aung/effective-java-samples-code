# Chapter 8 — Methods (items 49–56)

Building a clear, correct public API: validate what you accept, defend what you
hold, and design signatures and contracts people can't misuse. Round 1 covers
items 49–51; items 52–56 continue.

---

## Item 49 — Check parameters for validity

A method is only as safe as the values you let inside. Public methods and
constructors should assert their preconditions **at the boundary**: reject the
garbage the instant it arrives, with an exception that names the offending
parameter — not after silently corrupting internal state.

- `null` → `NullPointerException` (via `Objects.requireNonNull`) or a
  documented unchecked type.
- Out-of-range / non-finite values → `IllegalArgumentException` (report the
  actual value so the caller can fix it).
- Validate in **constructors** too — an object should never be born broken.
- Don't repeat the check deep inside; `private static` helpers keep one source
  of truth.

**Sample:** `BadDeposit.deposit` does `balance += amount` with no checks — a
negative or `NaN` deposit silently poisons the balance. `GoodDeposit` validates
a non-negative init balance in its constructor and requires strictly positive,
non-`NaN` amounts in `deposit` — state is only touched after the check passes.
The demo shows the bad account ending at `NaN` while the good one throws
explanatory `IllegalArgumentException`s.

---

## Item 50 — Make defensive copies when needed

Java passes **references**, not values. When a caller hands you a mutable object
(a `Date`, an array, a `List`), storing the reference directly means a future
mutation on *their* object silently changes *your* object — even a "final"
field won't save you. When a mutating caller can't be trusted (or there's no
safe shared value), **copy on the way in** (constructor/`add`) **and on the way
out** (getters).

- Copy in the constructor so the caller can't change your internals afterwards.
- Copy in the getter so a caller can't reach into your internals.
- Check validity *after* the copy so the original isn't half-copied on failure.
- The cleanest fix is the same reason we prefer `Instant`/`LocalDate` over
  `java.util.Date` and immutable collections over mutable `ArrayList`s: when
  the type itself can't mutate, no copy is needed.

**Sample:** `BadPeriod` stores caller `Date`s by reference and returns them
as-is — mutating `getStart().setTime(...)` rewrites the "immutable" period
(demo shows `lengthMillis` flipping negative). `GoodPeriod` copies in both the
constructor and both getters, so neither the caller's original `Date` nor the
returned copy can corrupt the period (demo keeps `1000`). `DefensiveCopyTest`
pins both directions.

---

## Item 51 — Design method signatures carefully

Four habits separate a usable signature from an effective ambush:

1. **Choose names that are hard to get wrong** and make the natural reading the
   correct one.
2. **Don't overload on boolean `flags`.** Three adjacent `boolean` parameters
   are a guessing game at the call site and invite transposition bugs.
   Replace each with a small `enum` (self-documenting, type-checked, and
   extensible).
3. **Don't go overboard on parameter count** — if a method needs many
   parameters, an immutable **builder** or a value object is usually clearer.
4. **Prefer interfaces as parameter types** and favour two `? extends T`/`?
   super T` uses of the collection kinds already picked.

**Sample:** `BadSignature.qualify` takes `name, region` plus *four* booleans —
the demo call `qualify("alice","us",true,true,true,false)` is unreadable and
the last flag silently flips the meaning of the previous three.
`GoodSignature.qualifies` replaces the flag soup with a
`Requirement[]` of named `enum` values, so intent reads from the call site and
the compiler rejects garbage.

> **Note — name your constants.** The chapter's original `BadCalculator`/`Good-
> Calculator` pair (a hard-coded `0.20` tax rate vs. a named
> `TAX_RATE` constant) is kept as the item-51 companion: give important values
> a name and reuse one source of truth instead of scattering magic numbers.

---

## Senior checklist

- [ ] Public methods/constructors validate at the boundary and report the
      offending value (49).
- [ ] Mutable inputs/outputs are defensively copied so callers can't reach
      internals (or use immutable types so none is needed) (50).
- [ ] No boolean-flag parameters; enums and builders make signatures readable
      and type-safe (51).

---

*Round 2 (52–56: overloading, varargs, empty returns, optionals, doc comments)
continues.*
