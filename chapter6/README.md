# Chapter 6 — Enums and Annotations (items 34–41)

Items 34–38 covered the *enum* side; items 39–41 move to *annotations* and the
marker-interface alternative.

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

## Item 39 — Prefer annotations to naming patterns

The tooling of the 90s used *naming patterns*: "a method is a test if its name
starts with `test`; mark it off with `tearDown`-style names". Every naming
pattern fails the same way — **a typo silently breaks the contract**:

- You forget the literal prefix (or misspell it): `tetsFoo` or `test_fool`.
  No compiler, linker, or runtime error — the "test" simply never runs, and
  your suite reports green.
- There is no way to *verify* you meant to mark a method, because the signal is
  just a string convention.

Annotations replace the pattern with metadata the compiler and tooling can
see: `@Test` is a *real type*, `@Retention(RUNTIME)` lets a runner reflect on
it, `@Target(METHOD)` restricts where it may go, and a typo in an annotation
name is a compile error, not a silent skip.

**Sample:** the repo defines `@GoodTest`, and two runners:
`BadNamingRunner` finds methods whose name starts with `test`; `GoodAnnotationRunner`
reflects only over `@GoodTest`-annotated methods. `NamingPatternTest` proves the
bad runner runs just 2 methods and silently skips `tetsMultiply` (which would
have thrown), while the good runner discovers all 3 annotated methods — 2 pass,
1 fails, and the failure is *reported*, not swallowed.

### Annotation lifecycle / meta-annotations (must-know)

- `@Retention` decides whether the annotation survives to runtime: `SOURCE`
  (compile-time only, e.g. `@Override`), `CLASS` (default, in bytecode but not
  visible to reflection), `RUNTIME` (visible to reflection — required for any
  runtime-inspecting framework like a custom runner or a DI container).
- `@Target` restricts where it can appear: method, type, field, parameter,
  etc. Restricting gives the compiler a chance to catch misuse (e.g. putting a
  `@MethodOnly` annotation on a class).

---

## Item 40 — Consistently use the Override annotation

`@Override` is the compiler's leash on your polymorphism. When you add it, the
compiler *forces* the method to actually override — if you misspelled the
signature, you get an immediate error.

Without it, the code still compiles, but **silently does the wrong thing**: you
wrote a *new* method that merely *hides* a superclass method of a different
name/signature, so calls through the base type never reach your implementation.
It compiles green, passes review, and only misbehaves at runtime in the least
obvious way. In an interface-implementing class the compiler warns about the
ambiguity; in a class hierarchy it's silent.

**Sample:** `BadShapeBase` has `name()`. `BadShapeName.Square` declares
`getName()` intending to override `name()`. `OverrideTest` shows why it fails:
`square.getName()` returns "square", but `((BadShapeBase) square).name()` still
returns "base" — the "override" never fired, and polymorphism broke. `GoodShape`
bows with `@Override public String name()` and the base-typed call correctly
returns "square".

---

## Item 41 — Use marker interfaces to define types

A *marker interface* (`java.io.Serializable`, `java.util.RandomAccess`) has no
methods — its entire job is to make "has this property" a **type**.

Annotations can mark things too, so why prefer an interface when possible?
Because an interface is a *type*:

- **Compile-time enforcement** — `save(GoodPersistable)` refuses any object
  that isn't a `GoodPersistable`; a `@Persistable` annotation is only a runtime
  hint that `save(Object)` must manually check (and a forgetful check means
  garbage is stored).
- **`instanceof` works** — you can branch on `entity instanceof RandomAccess`
  at runtime; there is no `instanceof` for an annotation.
- Annotation markers win when the property carries *values* (`@Path("/x")`,
  `@Column("name")`) or when retrofitting many existing classes without
  changing their type hierarchy.

**Sample:** `GoodPersistable` is an empty marker interface; `GoodRepository.save`
takes exactly that type, so a non-persistable object can't compile through it.
`BadPersistable` is a `@Retention(RUNTIME)` annotation and
`BadAnnotatedOnlyPersistence.save(Object)` accepts *anything*, then performs a
manual reflection check per call — late, permissive, and easy to forget.
`MarkerInterfaceTest` pins both: the good repository stores a `GoodPersistable`,
and the bad repository only rejects a non-annotated `Object` at runtime.

---

## Senior checklist

- [ ] Fixed categories are enums with behavior, never bare `int`s (34).
- [ ] No business meaning ever derived from `ordinal()` (35).
- [ ] Set-membership flags use `EnumSet`, not `int |` bit gouging (36).
- [ ] Enum-keyed lookups use `EnumMap`, never `[...ordinal()]` (37).
- [ ] Extensible "enums" are an interface + independent enum families (38).
- [ ] Framework hooks run on annotations with `RUNTIME` retention, not naming
      patterns (39).
- [ ] Every override wears `@Override`; the compiler enforces the contract (40).
- [ ] A property that is a *type* becomes a marker interface; a property with
      data becomes an annotation (41).

## Exercises

`exercises/chapter6.md` then check `exercises/chapter6-solutions.md`.

## Verify

```bash
mvn -q clean test
mvn -q exec:java '-Dexec.mainClass=chapter6.demo.EnumDemo'
```