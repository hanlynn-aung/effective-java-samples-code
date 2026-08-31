# Chapter 8 — Methods (items 49–56)

Building a clear, correct public API: validate what you accept, defend what you
hold, and design signatures and contracts people can't misuse. All eight items
(49–56) are built across two rounds:

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

## Item 52 — Use overloading judiciously

Overloads are resolved at **compile time** by the *static* type of the argument,
never by its runtime type. That makes overloaded families that are near each
other — same name, similar parameter types like `Set`/`List`/`Collection` — a
surprise factory: a `List` stored in a `Collection` variable picks the
`Collection` overload, which often isn't the behaviour the writer intended.

Rules to keep it safe and readable:

- **Never overload across unrelated functional types.** Same-number, same-name
  methods taking `Function` vs `Predicate` etc. are a trap.
- When overloads are genuinely at the same specificity, give each a **distinct,
  describing name** (`classifyAsList` vs `classifyAsSet`) so the call site
  states intent and no resolution ambiguity remains.
- Favour constructors-with-names (static factories — item 1) over a forest of
  overloaded constructors.

**Sample:** `BadReload` overloads `classify(Set|List|Collection)`. The test
shows a real `List` stored in a `Collection<String>` variable hits the
`collection` branch — surprising. `GoodDispatch` renames the List variant to
`classifyAsList`, so the outcome follows the name, not the compiler's
specificity tie-break.

---

## Item 53 — Use varargs judiciously

Varargs (`int... values`) compiles to an **array** and is perfect when the
callee wants an arbitrary, possibly-large number of homogeneous values. Its
hazards:

- The `T...` parameter accepts **zero** values; a method like `min()` that reads
  `values[0]` crashes with `ArrayIndexOutOfBoundsException` on no arguments.
  Require at least one value as a **separate leading parameter**
  (`int min, int... rest`) — the compiler then enforces "at least one".
- Every call allocates an array, so don't sprinkle varargs on hot paths that run
  in a loop with a single argument. Prefer an explicit size for performance,
  and only use `T...` where the variadic convenience genuinely earns the
  allocation (a tiny count of args).
- `null` and an empty array for `T...` behave differently — keep `List.of(...)`
  style discipline at call sites.

**Sample:** `BadVarargs.min(int...)` throws on `min()`. `GoodVarargs.min(int
first, int... rest)` makes the empty case unrepresentable, and
`VarargsTest` shows `min(2,5,3)` works while `sum()` (where zero is meaningful)
stays happily empty.

---

## Item 54 — Return empty collections or arrays, not nulls

A method that has "no results" should return an **empty collection/array**,
never `null`. Returning `null` forces every caller to null-check before
`for`/stream/`size()`, and any caller who forgets gets a surprise NPE in
production. Returning an empty `List.of()` / `new int[0]` is:

- **zero-cost**: `List.of()` and empty arrays are shared/cached, no per-call
  allocation.
- **safe to iterate**: `for` loops, streams, and `size()` all work with no
  branch.
- **self-documenting**: the contract "empty means no results" needs no prose.

**Sample:** `BadNullReturn.find("")` returns `null`; the test shows
`empty.size()` NPEs. `GoodEmptyReturn.find("")` returns `List.of()`, so
`size() == 0` and streaming/iteration are safe. `EmptyReturnTest` pins both.

---

## Item 55 — Return optionals judiciously

`Optional<T>` is suited to a **method whose return may legitimately be absent**
in a way the caller must handle — and little else. Three misuses to avoid:

- **Never wrap a collection.** An `Optional<List<T>>` is pointless: an empty
  `List` already encodes "none". Return the (possibly-empty) collection — item 54.
- **Never wrap a primitive.** Prefer the primitive-specific `OptionalInt`,
  `OptionalLong`, `OptionalDouble` to `Optional<Integer>` etc., which boxes
  just to carry an absence flag.
- **Never store nulls in optionals.** `Optional.of(x)` rejects `null` — that's
  a feature; encode absence as `Optional.empty()`.
- **Don't use `Optional` as a field type or an argument** — it's a *return-type*
  signal, and the book notes it can be a performance trap when the answer is
  almost always present.

Consume with `orElse`, `orElseGet` (lazy), `orElseThrow`, `ifPresent`, or the
`map/flatMap/filter` combinators — not by calling `.get()` after `.isPresent()`
(that's the smell the whole design avoids).

**Sample:** `BadOptionalOveruse` wraps a `List` (`Optional<List<String>>`) and a
primitive `Double`. `GoodOptional` returns the `List` directly, uses
`OptionalDouble` for a price, and reserves `Optional<String>` for a genuinely
absent middle substring, consumed via `orElse`/`orElseGet`. `OptionalTest`
checks each.

---

## Item 56 — Write doc comments for all exposed API elements

A public API's contract lives in its Javadoc. Give every exposed class,
interface, method and field a doc comment that answers *what it does*, the
**preconditions** (`@param`), the **postcondition** (`@return`), the exceptional
behaviour (`@throws`, or `@exception`), and caveats (`@implSpec`, `@implNote`,
`{@code}`, `{@link}`, and HTML-free prose).

Good docs earn their space:

- Document **implication of behaviour**, not re-state the parameters verbatim.
- Use `@throws` for every checked exception and any meaningful unchecked one.
- `{@code}`, `{@link}`, and `<pre>{@code ...}</pre>` fence snippets so output is
  literally correct.
- `@implSpec` states what a subclass must honour — the contract by which
  implementers inherit the method.
- If a method's documented contract is *not* honoured by the code, the doc
  comment is a lie; let the doc and the assertions agree (the sample's contract
  is pinned by a test).

**Sample:** `BadDocumented.rate` is a bare public method with no comment, hiding
its `IllegalArgumentException` precondition. `GoodDocumented.rate` carries
`@param`, `@return`, `@throws`, `@implSpec`, and `{@link Double#NaN}`.
`DocCommentTest` asserts the *documented* contract (throws on NaN/negative,
returns `price * TAX_RATE`) is the *actual* behaviour.

---

## Senior checklist

- [ ] Public methods/constructors validate at the boundary and report the
      offending value (49).
- [ ] Mutable inputs/outputs are defensively copied so callers can't reach
      internals (or use immutable types so none is needed) (50).
- [ ] No boolean-flag parameters; enums and builders make signatures readable
      and type-safe (51).
- [ ] Overloads don't sit at the same specificity; distinct names carry the
      intent (52).
- [ ] `T...` has a leading required parameter when "at least one" must hold (53).
- [ ] No `null` returns — empty collections/arrays for "no results" (54).
- [ ] `Optional` only for absent returns, never wrapping collections/primitives;
      consumed via `orElse`/`orElseThrow` (55).
- [ ] Every exposed API element has honest Javadoc: `@param`, `@return`,
      `@throws`, `@implSpec` (56).
