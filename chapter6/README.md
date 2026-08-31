# Chapter 6 — Enums and Annotations (items 34–41)

Round 1 covers the *enum* items (34–38); the annotation items (39–41) continue
below.

| Item | Title | Core idea |
|------|-------|-----------|
| 34 | Enums over int constants | An enum is a real type; a fixed int is a lie at the boundary |
| 35 | Instance fields over ordinals | Never persist meaning in `ordinal()` — it follows code order, not data |
| 36 | `EnumSet` over bit fields | A typed `EnumSet` rejects undefined members at compile time |
| 37 | `EnumMap` over ordinal indexing | Key your data by the enum, not by its array index |
| 38 | Extensible enums via interfaces | Enums can't extend enums — emulate it with an interface |
| 39 | Prefer annotations to naming patterns | JUnit-up your checks; naming patterns fail silently |
| 40 | Consistently use `@Override` | One missed annotation can silently stop overriding |
| 41 | Marker interfaces define types | A marker interface is a type your APIs can require |

---

## Item 34 — Use enums instead of int constants

Int constant patterns (`int READY = 1; int FAILED = 2;`) break down in every
dimension worth caring about:

- **No type safety** — `describe(999)` and `describe(-5)` compile and return
  "unknown"; there is no real 'status type' to violate.
- **No self-documentation** — you can't iterate them, you can't print a
  friendly value, and magic numbers leak into switch/if chains.
- **No behavior attachment** — the handling logic lives outside the values, in
  a chain of `if (x == READY)` that must be kept in sync by hand.

An enum gives you a genuine type with a fixed set of values, `values()` for
iteration, `name()`/`toString()` for output, and per-constant behavior (see
items 38). Comparing enums with `==` is safe and fast.

**Sample:** `BadStatus` accepts any `int`; `describe(999)` silently reports
"unknown". `GoodStatus` is an enum with one instance field (`description`) and
`values()` covers exactly the two real states. `EnumBasicsTest` pins the
garbage-int acceptance against the type-safe iteration.

---

## Item 35 — Use instance fields instead of ordinals

`ordinal()` tells you a constant's *position in the source*, which is a
code-layout accident, not data. Deriving business meaning from it (`ordinal()+1`
as a "rank") is the classic footgun:

```java
enum BadMerit { LOW, HIGH, MEDIUM; }   // someone inserts MEDIUM second
```

Now `HIGH.rank()` is 2 and `MEDIUM.rank()` is 3 — but the intended tiers are
HIGH=3, MEDIUM=2. Nothing complains; every caller silently gets wrong data.
Add a constant later and *every* ordinal shifts, corrupting persisted values.

The rule: **if you need a rank or database value, store it in an instance
field**, and design so declaration order never matters. Don't even depend on
`ordinal()` for internal bookkeeping when a field is clearer.

**Sample:** `BadMerit` computes `rank()` from `ordinal()+1` with `LOW, HIGH,
MEDIUM` declared in that order — `MEDIUM.rank()` returns 3 though it's the 2nd
tier. `GoodMerit` carries an explicit `tier` field (`LOW(1), HIGH(3), MEDIUM(2)`),
so the true ranking survives any reordering. `OrdinalTest` contrasts both.

---

## Item 36 — Use EnumSet instead of bit fields

The int-constant pattern's worst idea is the *bit field*: `int STYLE_BOLD =
1<<0;` combined with `|`. It compiles fast but is a leaky, silent abstraction:

- **Undefined bits slip through** — `apply(style | 128)` compiles; 128 isn't a
  style, so nothing renders. The caller gets a broken result with no error.
- **Nothing is self-describing** — printing `5` tells you nothing; there's no
  `toString` that says "bold + italic".
- **No iteration** — you can't ask "which of these styles are set?" without
  hand-rolled bit peeling.

`EnumSet` gives you the same performance (it's backed by a `long` bit vector for
≤64 values) but with a real type: you build it with `of`, `range`, or `complementOf`,
it iterates in declaration order, and it can only contain actual enum values.

**Sample:** `BadTextStyle` exposes `int` masks; `apply(BOLD|ITALIC|128)` silently
ignores 128 (the `EnumSetTest` asserts the garbage is accepted without error).
`GoodStyle.apply(Set<Style>)` takes an `EnumSet<Style>` — there is no way to
pass an undefined style, and `EnumSet.of(BOLD, ITALIC)` is readable and
self-describing.

---

## Item 37 — Use EnumMap instead of ordinal indexing

The ordinal footgun extends to *indexing*: fronting a value with an `int` key
meant to be an enum's `ordinal()`:

```java
LifeCycle[] buckets = new LifeCycle[LifeCycle.values().length];
```

Then `buckets[plant.lifeCycle().ordinal()]`. Problems creep in immediately:

- **No type safety** — any `int` in range is accepted; `33` compiles and
  `ArrayIndexOutOfBoundsException`s at runtime.
- **Silent holes** — a lifecycle that never appears in the data leaves a `null`
  (or an empty slot) the caller didn't ask for and can't easily tell apart.
- **Corruption by insertion** — add an enum constant and every index after it
  shifts, silently remapping every bucket.

`EnumMap` is the safe, fast (array-backed) analogue: you key by the enum type
itself, the keys are genuinely typed, missing keys are explicit (you can
pre-seed every `values()` entry), and no ordinal math ever appears.

**Sample:** `BadOrdinalGardener.classify` stores plants in a `List<Plant>[]`
indexed by `ordinal()`. `EnumMapTest` shows the annual bucket stays empty with
no hint. `GoodEnumMapGardener.classify` returns an `EnumMap` pre-seeded with
`new ArrayList<>()` for *every* lifecycle — pristine, typed, complete.

---

## Item 38 — Emulate extensible enums with interfaces

An enum is declared `final` — you cannot extend one. That's normally fine and
even desirable, but occasionally you want "the round, plus whatever the field
adds" (e.g. JDK `BasicOperation` vs `ExtendedOperation`). The book's idiom:
**emulate extensibility with interfaces.**

- Define an interface whose methods the enums implement (here `GoodOperation`
  with `symbol()` + `apply(...)`).
- One or more *enums* implement it (`GoodBasicOperation`, `GoodExtendedOperation`).
- **Windows accept the interface type**, so any implementing enum is accepted
  interchangeably and new families can be added without touching callers.

**Sample:** `BadFrozenOperation` is a closed enum — adding `EXP` requires
editing the shared source (a fork, or a build/merge conflict for library users).
`GoodOperation` is the interface; `GoodBasicOperation` and `GoodExtendedOperation`
are two independent enums implementing it. `ExtensibleEnumTest` and the demo
pass both kinds into one `compute(GoodOperation op, ...)` — `EXP`, `REMAINDER`,
`PLUS` all dispatch correctly.

---

## Senior checklist (round 1)

- [ ] Fixed categories are enums with behavior, never bare `int`s (34).
- [ ] No business meaning ever derived from `ordinal()` (35).
- [ ] Set-membership flags use `EnumSet`, not `int |` bit gouging (36).
- [ ] Enum-keyed lookups use `EnumMap`, never `[...ordinal()]` (37).
- [ ] Extensible "enums" are an interface + independent enum families (38).

## Exercises

`exercises/chapter6.md` then check `exercises/chapter6-solutions.md`.

## Verify

```bash
mvn -q clean test
mvn -q exec:java '-Dexec.mainClass=chapter6.demo.EnumDemo'
```