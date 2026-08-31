# Chapter 3 — Methods Common to All Objects

**Effective Java, Items 10–14.** Every class inherits `equals`, `hashCode`,
`toString`, `clone`, and `Comparable` awareness from `Object`. This chapter
is about overriding them correctly — the difference between a class that
"works" and one that silently corrupts `HashSet`s, reports useless log lines,
or breaks sorting.

Sources live in `src/main/java/chapter3/{bad,good}` with package
`chapter3.{bad,good}`.

## Item 10 — Obey the general contract when overriding equals

Override `equals` only for *value classes* — classes whose instances make
sense equal by their contents (`Point`, `Money`, `Person`). Don't override it
for "active" classes (`Thread`, `Service`, `Resource`): identity is the right
answer there.

The **Object contract** — five rules:

1. **Reflexive**: `x.equals(x)` is true.
2. **Symmetric**: `x.equals(y)` ⇔ `y.equals(x)`.
3. **Transitive**: `x.equals(y)` and `y.equals(z)` ⇒ `x.equals(z)`.
4. **Consistent**: repeated calls give the same answer (so **don't base
   equals on mutable fields**).
5. **Non-null**: `x.equals(null)` is false — check with `instanceof`, which is
   null-safe.

### Two classic violations

**Symmetry** — `BadCaseInsensitiveString.equals` special-cases `String`:

```java
BadCaseInsensitiveString cis = new BadCaseInsensitiveString("Foo");
cis.equals("foo");   // true  — this class knows about String
"foo".equals(cis);   // false — String doesn't know about this class
```

The contract holds only if reversing the operands gives the same answer;
mixing types in `equals` breaks symmetry. Fix: **don't compare to `String` at
all** — wrap consistently, keep the class's `equals` limited to its own type.

**Transitivity** — `BadTransitivityColorPoint extends BadTransitivityPoint`
where the base uses `instanceof` equality and the subclass adds a field:

```java
p.equals(red)  == true    // red is also a Point
p.equals(blue) == true    // blue is also a Point
red.equals(blue) == false // different colors
```

`p == red`, `p == blue`, `red != blue` — transitivity broken. This is the
fundamental problem with **value-based inheritance**: you can add state in a
subclass and keep a sensible base `equals`, but not both.

**Good practice** (`GoodPoint`):

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;                       // fast path
    if (!(obj instanceof GoodPoint other)) return false; // null-safe, type-check
    return x == other.x && y == other.y;                // compare fields
}
```

- Compare fields by `==` for primitives, `.equals` for immutable objects
  (a mutable etc. field must be compared defensively — see chapter 8).
- Compare *every* significant field, in an order likely to fail early.
- Always annotate `@Override`.
- Also make the class **immutable** (see chapter 2/3 theme) — a mutable
  value object's `equals` is inconsistent, and mutating an object inside a
  `HashSet` breaks lookups forever. `EqualsContractTest.mutableValueObjectBreaksHashSet`
  proves it with `BadMutablePoint`.

## Item 11 — Always override hashCode when you override equals

The contract (from `Object`):

1. Equal objects **must** have equal hash codes: `a.equals(b)` ⇒
   `a.hashCode() == b.hashCode()`.
2. Unequal objects *may* share a hash (collisions are legal — quality is
   about reducing them).
3. The hash must be consistent while the object is stored in a hash-based
   collection.

The failure mode is real: `BadHashPoint` overrides `equals` but not
`hashCode`. Two points that are `equals` land in different hash buckets, so a
`HashSet` happily stores **both** — duplicated "unique" elements, un-findable
lookups.

`GoodPoint` does it right — a 31-multiplier or `Objects.hash` over the same
fields, and because the class is **immutable**, it **caches the hash** once in
the constructor (the book recommends lazy initialization for expensive
hashes; caching is safe only for immutable fields):

```java
private final int hashCode;                       // cached on construction
public GoodPoint(int x, int y) {
    this.x = x;
    this.y = y;
    this.hashCode = Objects.hash(x, y);
}
@Override public int hashCode() { return hashCode; }
```

**Senior note**: `record`s generate `equals`/`hashCode`/`toString` for you
with exactly this semantics — one of the strongest reasons to reach for them
for value objects.

## Item 12 — Always override toString

The `Object.toString` default is `chapter3.bad.BadPoint@7f31245a` — a class
name and an identity hash. In a log line that is *nothing*: which point? what
values? A senior reads logs all day; a useful `toString` is an `@Override`
away (`GoodPoint[x=1, y=2]`).

Guidelines:

- Return a **complete but concise** representation of all significant fields.
- **Document the format** — and state whether the format is stable across
  versions (`GoodPoint[x=1, y=2]` today, `(1|2)` in the next release breaks
  anyone parsing it, so say so).
- Don't run side effects or throw while producing `toString` (debuggers,
  loggers, and `StringBuilder.append` all call it).
- Override `toString` too when you override `equals`: same value semantics,
  same readability expectations.

## Item 13 — Override clone judiciously (or rather: don't)

`Cloneable` is the *wrong* tool for copying. Why:

- `Object.clone()` **does not call a constructor** — it copy-allocates.
- It throws checked `CloneNotSupportedException` and destroys your typing;
- A shallow clone shares every mutable field with the original.

`BadShallowClonePerson` is the poster child: `clone()` calls `super.clone()`,
so the clone's `phones` **list is the same object** as the original's.
`CloneTest.badShallowCloneSharesInternals` mutates the clone's list and the
original sees the change — a timestamped "clone" that silently edits its
source.

The book's recommendation, which is also the modern one: **prefer copy
constructors or a static copy factory to `clone`**. `GoodCopyFactoryPerson`
does this without ever touching `Cloneable`. It takes its fields anew and
defensively copies the mutable list, so the copy is fully independent.

`Cloneable` still appears in a few places (array cloning, some JDK types) —
understand it, but don't reach for it in new code. For immutable objects, a
copy is unnecessary anyway (sharing is safe).

## Item 14 — Consider implementing Comparable

`Comparable` gives natural ordering — `Arrays.sort`, `TreeSet`, `TreeMap`,
`Collections.min`. The contract: `compareTo` must be `sgn(x.compareTo(y)) ==
-sgn(y.compareTo(x))`, **transitive**, and — unless you document otherwise —
**consistent with `equals`**: `x.compareTo(y) == 0` should mean `x.equals(y)`.

Two senior traps:

**Overflow.** `BadSubtractionComparator` returns `a.score - b.score`:

```java
compare(Item(MAX_VALUE), Item(-1))  // Integer.MAX_VALUE - (-1) overflows to negative
                                    // → claims MAX_VALUE < -1
```

Use `Integer.compare`, `Long.compare`, `Double.compare`, or
`Comparator.comparing(...)` — never subtraction.

**Inconsistency with `equals`.** `BadInconsistentPerson` compares only the
`name`; two people named "Han" with different scores compare `0` but are
`equals`-distinct. In a `TreeSet`, the second "Han" **silently replaces the
first** — one person lost from the collection. Sorted sets/maps are ordered
by `compareTo`, and equality never gets consulted.

`GoodComparablePerson` compares `score` then `name` with `Integer.compare`/
`String.compareTo`, and its `equals` checks exactly those two fields — so
`compareTo == 0` coincides with equality, and `TreeSet` keeps both distinct
persons.

## Senior checklist

- [ ] Override `equals`/`hashCode`/`toString` as a matched set for every
      value class; use `record` where shape allows.
- [ ] `equals` is symmetric/transitive — no mixing types, no value-based
      inheritance hacks.
- [ ] `hashCode` covers the same fields as `equals`; the class is immutable;
      cache the hash if computing it is non-trivial.
- [ ] `toString` prints all fields, is documented, and is side-effect-free.
- [ ] Copy operations are copy constructors / copy factories with deep copy,
      not `clone`.
- [ ] `compareTo` uses the boxed/`Comparator` comparators (no overflow) and
      is consistent with `equals`.

## Run it

```bash
mvn -q exec:java '-Dexec.mainClass=chapter3.demo.ObjectContractsDemo'
```

## Exercises

`exercises/chapter3.md` — seven tasks for items 10–14. Solutions in
`exercises/chapter3-solutions.md`.

## Further reading

- Effective Java items 10–14; the "prefer composition" warning against
  value-based inheritance is item 18 (chapter 4).
- Java 17 `record` — generates items 10–12 correctly by construction.