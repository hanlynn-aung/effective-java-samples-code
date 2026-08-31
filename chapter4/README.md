# Chapter 4 — Classes and Interfaces (items 15–25)

Items 15–19 are the *core design* round: encapsulation, accessors, immutability,
composition, and seriously deciding whether to let classes be subclassed.

| Item | Title | Core idea |
|------|-------|-----------|
| 15 | Minimize accessibility of classes and members | Hide everything you can; the smallest public surface is the best contract |
| 16 | Use accessor methods, not public fields | Private fields + accessors keep invariants enforceable |
| 17 | Minimize mutability | Immutable objects are simpler, shareable, and concurrency-safe |
| 18 | Favor composition over inheritance | Inheritance crosses encapsulation boundaries; delegation holds the contract |
| 19 | Design and document for inheritance...or prohibit it | A subclassable class is a promise; keep it or make the class `final` |

---

## Item 15 — Minimize the accessibility of classes and members

Information hiding (Parnas): a module hides *how* it works so it can evolve
without breaking clients. In Java that means four access levels, applied as
"make everything as inaccessible as possible":

| Level | Meaning | Think of it as |
|-------|---------|----------------|
| `private` | only the enclosing class | implementation detail |
| *package-private* (default) | any class in the same package | internal module surface |
| `protected` | package + subclasses | part of the public API *and* an extension contract |
| `public` | everyone | a promise you must keep forever |

The senior nuances most people miss:

- `protected` is **not** a middle ground — it commits you to the extension
  contract on top of the API, and clients can subvert invariants through it.
- The public API of a mature library is nearly frozen; every member you expose
  is a future maintenance cost. Expose behavior, never structure.
- Java 9 modules take this further: `module-info.java` can keep a class
  `public` inside the module while not exporting its package, so outsiders
  never see it at all.

**Sample:** `BadExposedLedger` hands out its internal `Map` as a public field —
any caller can `clear()` it and silently corrupt the accounts. `GoodCapsuleLedger`
keeps every field private, exposes only `record`/`balanceOf`/`total`, validates
its arguments, and even guards construction (`create()` + a private constructor).

**Check at code review:** can a caller break an invariant without reflection?
If yes, you exposed too much.

---

## Item 16 — In public classes, use accessor methods, not public fields

A public field is a write-through to your state — it can be set to anything,
any time. An accessor is a *gate*: validation lives in the constructor, values
change only through documented methods, and derived state stays consistent.

- Accessor naming in this repo follows the modern style: `headline()`,
  `writer()`, `articles()` (the style records use). Classic `getX()` is fine.
- Whenever a field is a mutable reference — a `List`, array, `Map` — the
  accessor must return a **defensive copy**; otherwise the caller can mutate
  your state through the getter. The one escape hatch: it's acceptable (and
  often preferable for performance) to expose public fields in a
  **package-private or private nested** class, where the outsiders are your
  own code.

**Sample:** `BadNewspaper` is a naked data bag — `paper.headline` can be
rewritten behind the paper's back. `GoodNewspaper` keeps fields private,
validates at construction, and `articles()` returns a fresh copy each call —
mutating the returned list changes nothing inside the object.

---

## Item 17 — Minimize mutability

The five rules, from the book:

1. Don't provide methods that modify the object (mutators/setters).
2. Make the class `final` (no subclass can add mutability).
3. Make every field `final`.
4. Make every field `private` (no outside code reaches in).
5. **Don't share mutable components** — use defensive copies for any mutable
   field, and never hand out your internal mutable references.

Why bother? Immutable objects are automatically safe to share, safe to put in
`HashSet`/`HashMap` keys, never need synchronization (chapter 10), and make
great cache keys. The famous trade-off: each value change needs a new object —
so provide *functional* update methods (`withX`) instead of setters.

**Sample:** `BadMutableTime` has `setHour`/`setMinute`; `GoodTime` is `final`,
fully `final`+`private`, validates its inputs, caches its `hashCode`, and
offers `withHour(...)`/`withMinute(...)` that return *new* instances. The
reflection test pins the declaration: every field `private final`, class
`final`.

**Watch out:** a class with `private` + `final` fields but a mutable *collection*
field is immutable only if the collection is never let out. That's why rule 5
exists — and why `record` components scare nobody: a record wrapping a `List`
is only immutable if the list is never exposed.

---

## Item 18 — Favor composition over inheritance

Inheritance violates encapsulation: a subclass depends on the *implementation*
of the superclass, not just its contract. The superclass can change its internals
in a release and silently break every subclass (the "fragile base class" problem).

Two concrete disasters, both in the samples and tests:

1. **The callback trap.** `BadInstrumentedHashSet` inherits `HashSet`. It
   overrides `add` and `addAll` to count. But `AbstractCollection.addAll` is
   implemented *in terms of `add`*, and `HashSet` didn't fix `addAll`, so
   `addAll(3 elements)` fires the counting twice: `getAddCount() == 6`.
   The fix isn't a cleverer subclass — you'd have to copy `HashSet`'s internals.
   The fix is composition: `GoodInstrumentedSet` wraps a `HashSet` and counts
   the delta in `size()` — exactly once per real addition, no double counting.
2. **The wrong `is-a`.** `BadStack extends ArrayList` advertises `List`
   capabilities a stack must never have: you can `add(0, "intruder")` into the
   middle and violate LIFO. `GoodStack` **contains** a `Deque` and exposes only
   `push`/`pop`/`isEmpty` — the contract you promised, and nothing more.

**When is inheritance right?** Only when `is-a` genuinely holds *and* the
superclass was designed for it (item 19).

---

## Item 19 — Design and document for inheritance, or else prohibit it

Subclassability is a design decision you make, not an accident you tolerate.
If you do it, you owe:
- *documentation* of how methods use overridable methods (self-use) and of
  what overridable methods must not do;
- and never call overridable methods from constructors, `clone`, or
  `readObject` — those contexts still run before the subclass's fields are
  initialized.

**Sample:** `FragileBaseCounter`'s constructor calls the overridable `add(1)`.
`BadExplodingCounter` overrides `add` to touch its own `bonuses` list — which
is still `null` while the super constructor runs. Result: **construction
itself throws `NullPointerException`.** Dynamically dispatched past the
subclass's initialization: the exact trap item 19 warns about.

**The other side:** if you're *not* designing for inheritance, say so and
enforce it. `GoodFinalCounter` is `final` — no hooks, no subclasses, simple and
safe. If you must leave hooks, make them `protected` empty *template* methods,
document their contracts, and keep them out of constructors.

**Senior framing:** every `protected` member and every non-`final` class is a
commitment to an extension contract. Prefer `final`; introduce hooks only when
a real use case demands them.

---

## Senior checklist

- [ ] Can a client break an invariant without reflection? If yes, reduce the
      public surface (item 15).
- [ ] Every read of mutable state goes through a defensive-copy accessor (16).
- [ ] Immutable-ish classes are `final`, fields `private final`, no setters (17).
- [ ] Before subclassing, asked: "is this genuinely `is-a`, and was the
      superclass designed for it?" Otherwise, compose (18).
- [ ] Non-final classes document their self-use and never call overridable
      methods from constructors (19).

## Exercises

`exercises/chapter4.md` then check `exercises/chapter4-solutions.md`.

## Verify

```bash
mvn -q clean test
mvn -q exec:java '-Dexec.mainClass=chapter4.demo.ClassDesignDemo'
```