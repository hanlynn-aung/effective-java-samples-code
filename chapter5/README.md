# Chapter 5 — Generics (items 26–33)

Generics are all about moving type errors from *runtime* to *compile time*.
Round 1 covers the type-level foundations (items 26–29); items 30–33 (generic
methods, wildcards, varargs, heterogeneous containers) continue below later.

| Item | Title | Core idea |
|------|-------|-----------|
| 26 | Don't use raw types | Raw `List` loses every type guarantee; the error shows up at the cast, not the write |
| 27 | Eliminate unchecked warnings | Suppression is a promise of safety — keep it minimal, disprove the risk |
| 28 | Prefer lists to arrays | Arrays are covariant + reified; generics are invariant + erased — the two do not mix |
| 29 | Favor generic types | A generic class needs no casts and cannot be mis-stored by clients |

---

## Item 26 — Don't use raw types

A raw type is the generic type with the type arguments removed: `List` instead
of `List<String>`. It exists *only* for compatibility with pre-generics code.
Using one throws away the safety the compiler gives you — a value of a wrong
type is silently accepted and only explodes later, at the site of a cast or
assignment.

Compare the three shapes people confuse:

- `List` (raw) — no type parameter at all; *anything* goes, unchecked warnings, heap pollution risk.
- `List<Object>` — a genuine type; it says "this list may hold any object", which is explicit and safe.
- `List<?>` — the *unbounded wildcard*; "some specific type I don't know", safe to *read* as `Object`, safe to pass around.

**Sample:** `BadNumbers.values()` is a raw `List` — it happily stores `"not a
number"`. `GoodNumbers.values()` is `List<Integer>` — the compiler stops the
mismatch. `RawTypeTest` shows the bad list throwing `ClassCastException` at
read time (nothing at write time), while the good list can never leak a string.

**When you MAY use raw types:** `instanceof` (`list instanceof List`), and
solely for reading legacy code you cannot re-parameterize. Everywhere else,
reach for a parameterized type.

---

## Item 27 — Eliminate unchecked warnings

`javac -Xlint:all` is your hygiene tool. Every unchecked warning you leave is a
potential `ClassCastException` you've chosen not to hear about. Item 27 gives
you an order of operations:

1. **Eliminate the warning.** Redesign the code so the unsafe cast simply doesn't
   exist (transform values instead of relabeling a shared heap object).
2. **When it can't be eliminated**, suppress — but only at the smallest scope
   (a local variable, then a short method; never a whole class or package) and
   with the one-line proof that the cast is safe. The canonical example is the
   `Object[]` → `E[]` idiom in `GoodGenericStack` (item 29): private storage,
   only `E` values enter, cast read-back once at the bottom of `pop()`.

**Sample:** `BadWholeClassSuppression` hides a raw-typed field *and* an
unchecked cast behind a class-level `@SuppressWarnings` — a `String` lands
inside what is loudly "a `List<Integer>`", and the failure only surfaces at the
caller's cast. `GoodScopedSuppression` solves the *same* scenario by redesign:
it genuinely converts each value into an `Integer`, so no cast and no warning
ever existed.

Tests can't assert compiler warnings; the tests here pin the *behavior* — that
the bad suppression survived a corrupted store, and the good one round-trips
correctly.

---

## Item 28 — Prefer lists to arrays

Two deep differences make arrays and generics incompatible:

- **Arrays are covariant**: `String[]` *is* an `Object[]`. **Generics are
  invariant**: `List<String>` is *not* a `List<Object>`. Covariance is how
  `Object[] o = new Long[2]; o[0] = "s";` compiles — and then throws
  `ArrayStoreException` at runtime, because arrays still check element types
  at runtime.
- **Arrays are reified** (know their element type at runtime); **generics are
  erased** (their type exists only at compile time). Because erasure couldn't
  guarantee an array's element type, `new List<String>[10]` and `new E[10]`
  are **illegal — you cannot create generic arrays.**

Compile-time errors (generics) beat runtime errors (arrays): the generic `List`
rejects `Long` stored as a `String` before the program ever runs.

**Sample:** `BadCovariantArray.covariantTrap()` leaks a `String` into a
`Long[]` through covariance — the failure is deferred to the store, after the
variable was passed around. `GoodNumberList` uses `List<Number>`, where mixed
`Integer`/`Double` values are legal and every read is type-safe.

**Rule of thumb:** return and accept `List` (or `Set`/`Map`/…); use arrays only
at the deepest internal storage, cast once, and never let them cross your API.

---

## Item 29 — Favor generic types

Design the class generic from day one, and the whole class becomes cast-free:
callers receive exactly the type they asked for, and mixing types becomes
impossible at compile time. This is where items 27 and 28 meet: a generic class
that stores elements in an array must perform the one *legal* unchecked cast —
`Object[]` to `E[]` — in the narrowest scope with the safety comment.

**Sample:** `BadObjectStack` is a raw `Object` stack; the client must cast on
every `pop`, and one mis-pushed value (`push(7)` into a "string stack")
survives until the cast. `GoodGenericStack<E>` is truly generic — `push(E)`,
`pop()` returns `E` with no cast, `Object[]` storage casts once inside, and the
elements array never escapes the class.

The private-field argument applies doubly here: keep the `E[]` (or `Object[]`)
storage `private`, so nothing outside the class can ever observe it typed
wrongly.

---

## Senior checklist (round 1)

- [ ] No raw types in signatures, fields, or literals (26).
- [ ] `-Xlint:all` surfaces zero unchecked warnings; every suppression is
      single-statement and explained (27).
- [ ] Public API returns collections, not arrays (28).
- [ ] Every "stack/box/container" class is generic from the first commit (29).

## Exercises

`exercises/chapter5.md` then check `exercises/chapter5-solutions.md`.

## Verify

```bash
mvn -q clean test
mvn -q exec:java '-Dexec.mainClass=chapter5.demo.GenericsTypesDemo'
```