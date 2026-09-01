# Chapter 9 — General Programming (items 57–68)

The craft of everyday code: scoping, loops, libraries, and exact arithmetic.
All twelve items (57–68) are built across three rounds.

---

## Item 57 — Minimize the scope of local variables

The more code that can *see* a variable, the easier it is to hold a stale value
or accidentally reuse it for the wrong purpose. The rule is two-fold:

1. **Declare a variable only where its value is first ready** — not before.
   Declaring a loop index or accumulator way above its use widens the window for
   error and invites it to be read before/after it's meaningful.
2. **Declare it in the smallest block that needs it.** Prefer a `for`-loop index
   declared *inside* the `for` header, so its scope is exactly the loop. Fields
   that only one method touches are a smell; make such state a local.

**Sample:** `BadWideScope.sum` declares the index `i` before the loop and keeps
the running total in a **field** `total` — both outlive the loop, so `i` can be
re-read as a "loop count" and `total` survives as mutable shared state.
`GoodNarrowScope` folds the index into a `for` and keeps `total` a local that
dies with the method.

---

## Item 58 — Prefer for-each loops to traditional for loops

When you need every element of an array, `Collection`, `Iterable`, or iterator,
a **for-each** loop is strictly clearer and safer than the indexed form:

```java
for (String name : names) { ... }        // right: no index, no off-by-one
for (int i = 0; i < names.size(); i++)    // needless machinery + re-get() each pass
```

Why it wins:
- **No index** to declare, mutate, or get wrong — no `i < size()`, no off-by-one.
- **Only needs `Iterable`**, not indexing: it works for `List` *and* `Set`,
  arrays, and any iterator — an indexed loop can't even compile for a `Set`
  that has no `get(i)`.
- **Reads naturally:** "for each name, do X".

Reserve the traditional loop for the rare case you genuinely need the index or
must modify the collection while iterating (and even then prefer an explicit
`ListIterator`).

**Sample:** `BadIndexLoop.countEmpty` walks a `List` with an index and `get(i)`;
`GoodForEach` takes a `Collection` and uses a for-each, so it counts empty
strings in a `Set` (which has no `get`) exactly as easily as a `List`.
`LoopTest` proves identical results on a `List` and a working `Set` case the
indexed version could not express.

---

## Item 59 — Know and use the libraries

The JDK is a huge, well-tested library: `java.lang`, `java.util`,
`java.util.concurrent`, `java.time`, `java.util.stream`. Reusing it means
borrowing decades of correctness review instead of shipping your own bugs.
Know, at minimum, the core packages, and *search before you write* — a
reinvented wheel is slower to write, harder to read, and subtly wrong.

**Sample:** `BadReinvent` reimplements things the JDK already does well:
`Math.random() * bound` (a rolling-your-own range that invites bias/edge bugs)
instead of `ThreadLocalRandom.current().nextInt(bound)`; a manual `+=` join
instead of `String.join`; a hand-rolled `max` instead of `Math.max`.
`GoodUseLibraries` uses the JDK versions directly. The lesson: prefer
`ThreadLocalRandom`, `Collections`, `Integer.reverse`/`Math.*`, `String.join`,
`List.indexOf`, and friends before writing it yourself.

---

## Item 60 — Avoid float and double if exact answers are required

`float`/`double` are binary floating point — they cannot represent most decimal
fractions exactly, so **money (and any decimal-exact domain) must not use
them.** `0.1 + 0.2` in double is `0.30000000000000004`; adding `0.10 + 0.20 +
0.30` gives `0.6000000000000001`, not `0.60`. Cents silently accumulate into
discrepancies on statements, tax and totals.

Fix with:
- **`BigDecimal`** (from `String` or via `BigDecimal.valueOf`, never
  `new BigDecimal(0.1)`), or
- amount in **integer minor units** (cents, pennies) as `long`/`int` — exact and
  fast, appropriate for many APIs.

`BigDecimal` also lets you control **rounding and scale** (`setScale(2,
RoundingMode.HALF_UP)`) — decide rounding explicitly at the boundary.

**Sample:** `BadDollarsDouble` stores a balance as `double`; the demo shows
`0.10 + 0.20 + 0.30 = 0.6000000000000001`. `GoodDollarsBigDecimal` keeps an
exact `BigDecimal` built from `String` amounts, and `MoneyTest` asserts the
drift (`0.6000000000000001`) versus the exact `0.60`.

---

## Item 61 — Prefer primitive types to boxed primitives

Boxed types (`Integer`, `Long`, `Double`, …) exist to let primitives live in
generic containers; they are **not** a "safer Integer". Three ways they bite:

1. **`==` is identity, not value.** `sameRank(1000, 1000)` compares object
   references, so it's `false` even with equal numbers (values −128…127 happen
   to be cached and compare equal — silently, inconsistently).
2. **Unboxing `null` throws `NullPointerException`.** `value > best` where
   `value` is a `null` `Integer` NPEs at the comparison.
3. **`==`/`!=` on two boxed operands uses identity**, while `>=`/`<=` unboxes —
   mixing the two semantics in one expression is a notorious trap.

When to still box: as **type parameters** of generics (`List<Integer>`) and in
arrays of generic type — there, the box is unavoidable. Everywhere else use the
primitive.

**Sample:** `BadBoxedTrap` compares `Integer`s with `==` (identity) and unboxes
into a comparison (`value > best`), NPE-ing on a `null` value.
`GoodPrimitives` uses `long`/`int` everywhere plus `Long.compare`,
`Integer.MIN_VALUE` sentinels, and `==` on primitives (true value semantics).
`BoxedPrimitiveTest` pins the identity vs. value and null-unboxing failures.

---

## Item 62 — Avoid strings where other types are more appropriate

Strings are seductive — they print nicely and need no import — but a
`String` used as a *state, a type, a key, or an identifier* is a weak, silent
contract:

- **Type safety:** nothing stops a caller typing `"Ready"` vs `"ready"`; a
  string literal typo or casing difference silently changes behaviour.
- **No structure:** there's no compiler to catch a third bogus value.
- **Perf/parsing:** string keys need hashing/parsing instead of value equality.

Use the right type: an **`enum`** for a bounded set of states/kinds (also gives
you `switch`, `values()`, and later attached behaviour — item 34); a value
object / `record` for structured data; `UUID` or a typed id for identifiers.

**Sample:** `BadStringState` models a status with `"PENDING"`/`"READY"` magic
strings — `isReady()` and `advance()` depend on exact literal matches, so
`"Ready"` silently misbehaves. `GoodTypedEnum` models `Status` as an `enum`;
the compiler enforces valid values and casing is irrelevant.
`StringVsTypeTest` shows the bad string typo returning `false` while the enum
path is unambiguous.

---

## Item 63 — Beware the performance of string concatenation

`String` is immutable, so `result += part` in a loop **copies the entire
accumulating string every iteration** — O(n²) total for a loop building an
O(n) result. The demo measured **407 ms** (`+=` in a 50k loop) vs **4 ms**
(`StringBuilder`, O(n)) — a ~100× difference at moderate size.

Fix with:

```java
StringBuilder sb = new StringBuilder(expectedCapacity);
for (...) { sb.append(part); }
return sb.toString();
```

Prefer an **initial capacity** so the buffer doesn't grow and recopy. (A single
`a + b` or two/three `+` is fine — the compiler already rewrites it; the trap is
accumulating *in a loop*.) Java 15+ also offers `String.repeat` and text blocks
for some of these cases.

**Sample:** `BadConcatLoop.repeat` accumulates with `+=`; `GoodStringBuilder`
pre-sizes a `StringBuilder`. `ConcatTest` proves identical output at both small
and 10k-repeat sizes; the demo times the quadratic vs. linear paths so you can
see (not guess) the gap.

---

## Item 64 — Refer to objects by their interfaces

Declare fields, parameters, return types, and variables as the **interface / base
type** (`List`, `Map`, `Collection`, `CharSequence`, `InputStream`), not the
concrete class (`ArrayList`, `HashMap`). A `List`-typed program can be given
`ArrayList`, `LinkedList`, `CopyOnWriteArrayList`, or a view — its behaviour is
locked to the contract, not an impl, so:

- callers can choose the implementation that fits (concurrency, ordering, size).
- you can swap internals later **without touching callers** (a signature change
  is the disaster a concrete type forces).
- readers reason against the stable contract, not a mutable implementation.

Only use a concrete type when the interface lacks a feature you truly need
(e.g. `LinkedHashMap`'s iteration order, `PriorityQueue`'s heap-ish access).

**Sample:** `BadConcreteType` declares `ArrayList`/`HashMap` — the constructor
signature forces callers to supply exactly those. `GoodInterfaceType` declares
`List`/`Map`, so the test passes a `LinkedList`/`TreeMap` with no code change.
`InterfaceTypeTest` demonstrates both behave identically across impls.

---

## Item 65 — Prefer interfaces to reflection

Reflection lets you discover and call arbitrary code at runtime
(`Class.forName`, `getMethod`, `invoke`). It costs: **compile-time type checks
are lost** (a typo'd class or method name surfaces only as a runtime
exception), performance is worse, and it bypasses encapsulation. Prefer a
**compile-time interface** wired by dependency injection or
`ServiceLoader` — the compiler catches a wrong name, and callers see a real
contract.

Reflection has legitimate niches: runtime plugins/serializers, frameworks that
must inspect unknown classes. Reserve it for those, isolate the introspection
behind one interface, and never spread `getMethod` calls through application
code.

**Sample:** `BadReflective.buildGreeting` looks up a class by string, finds its
constructor and `greet` method by name, and invokes reflectively — a typo in
the class name throws only at runtime. `GoodInterfaceInvocation` depends on the
`Greeter` interface with a `FriendlyGreeter` implementation; `ReflectionTest`
shows the good path working (compile-checked) vs. the bad path failing at
runtime.

---

## Item 66 — Use native methods judiciously

Native (JNI) methods trade Java's safety for speed or OS access, but carry
serious costs: **no portability** (native code doesn't run everywhere), **no
memory safety** (a native bug can crash the whole JVM), harder debugging/GC
interaction, and complex build tooling. Before reaching for native code, ask:
does the JDK already do this in Java? — `System.currentTimeMillis()`,
`File`/NIO, `java.util.zip`, `ProcessBuilder`, `ThreadLocalRandom`, etc.

Use native only for true needs the JDK can't meet: calling an existing native
library (legacy C syscalls, hardware drivers, platform APIs), or a
provably-hot routine where Java proves too slow. Make the native call a thin,
isolated wrapper so the risk doesn't leak everywhere.

**Sample:** `BadAlwaysNative` "optimises" uppercasing and getting the time by
going native — when `String.toUpperCase()` and `System.currentTimeMillis()` are
pure Java. `GoodJavaFirst` uses exactly those; `NativeTest` shows the bad
bridges rejected while the good Java calls work.

---

## Item 67 — Optimize judiciously — measure first

Donald Knuth's rule: make it correct, then make it fast, and **only after
measuring** (a profiler, not vibes). Three traps:

1. **Premature complexity.** "Optimising" an unmeasured path with bit-twiddling
   or hand-rolled caches makes code harder to read with no proven payoff.
2. **Optimising the wrong thing.** 90% of time is usually in a few hot spots;
   without profiles you optimise code that was never slow.
3. **O(n²) blind spots.** Prefer the algorithmic/large-scale fixes (item 58
   for-each, item 63 `StringBuilder`, item 59 libraries) over micro-constants.

Keep the simple, correct version; if a profile shows a real hot spot, optimise
*that*, keep the alternative behind a test that proves it's still correct, and
re-measure the win.

**Sample:** `BadPreoptimize.sumFirst` adds `& Long.MAX_VALUE` (pointless
"optimisation" on an unmeasured path). `GoodMeasureFirst` writes the plain,
readable for-each. `OptimizeTest` and the demo show **they produce the identical
result** — the bad version only added obscurity, not speed.

---

## Item 68 — Adhere to generally accepted naming conventions

Names are the first contract a reader meets. Follow the platform conventions
so code is greppable, self-explanatory, and idiomatic:

- **Types:** `PascalCase` — `ShoppingCart`, `HttpClient`, `FooBar`.
- **Methods & fields:** `camelCase` — `calculateTotal`, `orderDate`.
- **Constants:** `CONSTANT_CASE` — `MAX_DAYS`, `DEFAULT_TIMEOUT_MILLIS`.
- **Local variables:** `camelCase`, full words over single letters (`limit`
  over `n`) — abbreviate only for a well-known short scope.
- **Packages:** lowercase, reversed domain (`com.acme.widgets`).
- **Patterns & contract methods:** conventional suffixes/verb names —
  `getX`/`setX`, `isX` for booleans, `hasX`, `size()`, `toString()`.

A bad name forces every reader to decode it every time; a good name states its
meaning once.

**Sample:** `BadNaming` uses `n`, `x`, `gv()`, `st()`, `gt()`, `chk()` — the
demo asks "what does `chk()` return?". `GoodNaming` uses `limit`, `value`,
`getLimit()`, `setValue()`, `isAtOrAboveLimit()` — self-evident. Both do the
same work; `NamingConventionTest` verifies the good one also validates its
`limit` (a named constant `MINIMUM`).

---

## Senior checklist

- [ ] Variables declared where ready, in the smallest block that needs them (57).
- [ ] Every element-access is a for-each unless the index or a mutation genuinely
      requires more (58).
- [ ] Prefer the JDK library to hand-rolled logic; search before writing (59).
- [ ] Money/decimal-exact values are `BigDecimal` or integer minor units, never
      `float`/`double` (60).
- [ ] Primitives by default; box only for generic type parameters (61).
- [ ] No magic strings for states/kinds/ids — use enums and value types (62).
- [ ] Build strings with `StringBuilder` (pre-sized) inside loops, never `+=` (63).
- [ ] Refer to objects by interface (`List`/`Map`) so implementations can vary (64).
- [ ] Prefer compile-time interfaces/DI to reflection; only introspect where you
      truly need runtime discovery (65).
- [ ] Reach for native methods only where Java can't do it — never for what the
      JDK already provides (66).
- [ ] Optimise after profiling the measured cost; keep code correct and readable
      first (67).
- [ ] Follow conventional naming: `PascalCase` classes, `camelCase` methods/fields,
      `CONSTANT_CASE` static finals, full words (68).
