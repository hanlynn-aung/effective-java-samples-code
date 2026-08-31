# Chapter 9 — General Programming (items 57–68)

The craft of everyday code: scoping, loops, libraries, and exact arithmetic.
Round 1 covers items 57–60; rounds 2 and 3 continue through item 68.

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

## Senior checklist

- [ ] Variables declared where ready, in the smallest block that needs them (57).
- [ ] Every element-access is a for-each unless the index or a mutation genuinely
      requires more (58).
- [ ] Prefer the JDK library to hand-rolled logic; search before writing (59).
- [ ] Money/decimal-exact values are `BigDecimal` or integer minor units, never
      `float`/`double` (60).

---

*Rounds 2 (61–64) and 3 (65–68) continue.*
