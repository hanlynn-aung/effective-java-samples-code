# Chapter 5 — Generics (items 26–33)

Generics are all about moving type errors from *runtime* to *compile time*.
Items 26–29 built the type-level foundations (raw types, warnings, arrays,
generic types); items 30–33 push into method design: generic methods, bounded
wildcards, safe varargs, and the typesafe heterogeneous container pattern.

| Item | Title | Core idea |
|------|-------|-----------|
| 26 | Don't use raw types | Raw `List` loses every type guarantee; the error shows up at the cast, not the write |
| 27 | Eliminate unchecked warnings | Suppression is a promise of safety — keep it minimal, disprove the risk |
| 28 | Prefer lists to arrays | Arrays are covariant + reified; generics are invariant + erased — the two do not mix |
| 29 | Favor generic types | A generic class needs no casts and cannot be mis-stored by clients |
| 30 | Favor generic methods | Methods, not just classes, should be generic — and get free type inference |
| 31 | Use bounded wildcards | PECS: producer-extends, consumer-super opens up your API |
| 32 | Combine generics & varargs | Treat varargs as save/restore of raw type info — never leak the array |
| 33 | Typesafe heterogeneous containers | `Class<T>` keys turn a single container into many per-type slots |

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

## Item 30 — Favor generic methods

Classes are the obvious place for type parameters, but *methods* deserve them
just as much. A generic method infers its type argument from the arguments you
pass — no casts on the caller's side, and the compiler checks the whole call in
one shot. Two shapes the senior must know:

- **Simple type parameter**: `<E> Set<E> union(Set<E> a, Set<E> b)` — one type
  parameter, inferred, result typed `Set<E>`.
- **Recursive type bound**: `<E extends Comparable<? super E>> E max(...)` —
  says "E is a type that can compare to itself", the signature behind
  `Collections.max`. The `? super E` (a consumer nuance) lets a `Number` list
  be maxed even though `Comparable<Number>` is implemented by `Integer`.

**Sample:** `BadSetHelpers` returns a raw `Set` from `union` and a raw
`Comparable` from `max`; callers cast — and the `GenericMethodTest` proves that
a wrongly-typed set survives until instantiation blows up mid-iteration.
`GoodSetHelpers.union`/`max` are generic: inference handles the rest, zero
casts, an `Integer` can never sneak into a `Set<String>`.

---

## Item 31 — Use bounded wildcards to increase API flexibility

The biggest flexibility squeeze in generic APIs: a `Chooser` that takes
`Collection<T>` cannot be built from a *producer* of a narrower type
(`new Chooser<Number>(List<Integer>)` fails to compile), and a copy method with
`List<E>` on both sides cannot copy `List<Integer>` into `List<Number>`.

The mnemonic is the whole item: **PECS — producer `extends`, consumer `super`.**

- If a parameter *gives you* values to read, it's a **producer**: `List<? extends E>`.
- If a parameter *takes* values from you to store, it's a **consumer**: `Collection<? super E>`.
- A parameter that does both (rare with immutable collections) must stay `E`.

**Sample:** `BadRigidCopy.copy(List<E>, List<E>)` rejects the perfectly safe
`List<Integer> → List<Number>` copy; `BadRigidChooser<T>(Collection<T>)`
rejects a `List<Integer>` producer. `GoodCopy.copy(List<? extends E>,
List<? super E>)` makes both directions legal and fully checked
(`Integer → Number`, `Number → Object`); `GoodChooser<T>(List<? extends T>)`
accepts *producers* of any narrower type. The WildcardTest pins the
`Integer→Number` and `Number→Object` copies that the Bad samples refused.

Never let `? extends` or `? super` appear on a *return type* that leaks mutable
state — it would force callers to cast.

---

## Item 32 — Combine generics and varargs judiciously

Varargs are implemented with arrays — and generic arrays are illegal. So every
call to a *generic* varargs parameter (`List<String>... lists`) silently
creates a heap array at the call site, and the compiler prints a warning. The
contract of item 32:

- **Always call** a non-reifiable-varargs method: the vendor made it safe.
  (`List.of`, `Collections` utilities.)
- **Author one only if you never touch the array**: the method must neither leak
  the varargs array nor perform an unsafe cast on it.

If the method is safe, annotate it `@SafeVarargs` (allowed only on `static`,
`final`, `private`, or constructor methods — since Java 9, `private` too). The
annotation tells callers their heap pollution warning is unjustified.

**Sample:** `BadHeapPollution.dangerous(List<String>... lists)` writes
`List.of(42)` into the caller-provided array slot zero, then reads slot zero as
a `String` — input type `List<String>` → runtime `ClassCastException`.
`GoodSafeVarargs.flatten` reads the array and copies every element into a fresh
`List<T>` that it owns; the array is never exposed, so it is `@SafeVarargs` with
a clean conscience.

---

## Item 33 — Consider typesafe heterogeneous containers

Normally a generic container ("a `Set<E>`") holds one type per container.
Sometimes you want a container that holds *many* types, each type-checked at
its own slot. That's the *typesafe heterogeneous container pattern*: the key is
the `Class<T>` object itself, and the magic trick is `type.cast(...)` — a
runtime cast performed by the `Class` you already hold.

- `put(Class<T> type, T instance)` — the key and value travel together.
- `get(Class<T> type)` — returns `T` via `type.cast(favorites.get(type))`.
- A raw-interference caller (`put((Class)String.class, 42)`) gets a
  `ClassCastException` **inside `put`** — the runtime check happens at the
  boundary, not at some distant read.
- Limitation: non-reifiable types (`List<String>.class` doesn't exist) can't be
  keys — work around with a supertype token or a full implementation.

**Sample:** `BadStringKeyedFavorites` keys everything with a `String` and
stores `Object` — the value's type is a rumour that each caller re-casts
(`(String) favorites.get("favorite number")` on an `Integer` → CCE).
`GoodFavorites` is the book's `Favorites` container: `put(String.class, "java")`
and `put(Integer.class, 42)` coexist, both `put` and `get` are runtime-checked,
and missing keys return `null` safely.

---

## Senior checklist

- [ ] No raw types in signatures, fields, or literals (26).
- [ ] `-Xlint:all` surfaces zero unchecked warnings; every suppression is
      single-statement and explained (27).
- [ ] Public API returns collections, not arrays (28).
- [ ] Every "stack/box/container" class is generic from the first commit (29).
- [ ] Static utility methods are generic with recursive bounds where needed —
      no raw returns, no caller casts (30).
- [ ] Producers use `? extends`, consumers use `? super`; never on leaky
      return types (31).
- [ ] Generic varargs methods are `@SafeVarargs` and never touch their array
      after capturing it (32).
- [ ] Any single-key heterogeneous data uses `Class<T>` keys with `type.cast`
      at the boundary (33).

## Exercises

`exercises/chapter5.md` then check `exercises/chapter5-solutions.md`.

## Verify

```bash
mvn -q clean test
mvn -q exec:java '-Dexec.mainClass=chapter5.demo.GenericsTypesDemo'
```