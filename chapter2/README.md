# Chapter 2 — Creating and Destroying Objects

**Effective Java, Items 1–9.** This chapter is built item by item; the doc
covers **Items 1–9**.

Sources live in `src/main/java/chapter2/itemN/{bad,good}` with package
`chapter2.itemN.{bad,good}`.

## Item 1 — Consider static factory methods instead of constructors

The full deep-dive lives in `chapter1/README.md`. Recap of the four benefits:

1. **Named** — `connectedTo(endpoint)` reads better and documents intent.
2. **Instance control** — cached or singleton instances instead of fresh
   objects every call.
3. **Subtype return** — the concrete class can stay hidden behind an
   interface.
4. **Varying/evolving return type** — different inputs give different
   implementations, and new ones can be added without breaking callers.

Downsides: harder to find (conventions help), and no subclassing without a
public/protected constructor (usually desirable — see chapter 4).

### Example: `BadStaticFactoryService` vs `GoodStaticFactoryService`

- **Bad** — public constructor; `new BadStaticFactoryService(null)` succeeds
  and the broken endpoint surfaces only at first use, far from the cause.
- **Good** — private constructor; static factory `connectedTo(endpoint)`
  rejects `null` immediately with `Objects.requireNonNull`, and reads as an
  instruction at the call site.

## Item 2 — Consider a builder when faced with many constructor parameters

### The problem

Many parameters, many of them optional. Three ways to screw this up:

**1. Telescoping constructors.** One constructor for each parameter count.
Hazards: hard to read (same-typed params are indistinguishable), silent
*swap bugs* (two `String` args in the wrong order compile clean), and every
call site is an argument-counting exercise. See `BadTelescopingUser`.

**2. JavaBeans setters.** A no-arg constructor plus setters. Hazards: the
object is *observable in an inconsistent state* between setters (a concurrent
thread can read a half-built object), it stays **mutable forever**, and
nothing enforces required fields. It can never be immutable. See
`BadBeanPizza`.

**3. The builder.** Fluent methods on a nested builder + a final `build()`.
Named parameters can't be swapped, all fields are final, and validation
happens once, atomically, in `build()`. See `GoodBuilderUser`.

### Records vs builders (Java 17)

```java
public record UserRecord(String name, String email, String phone) { }
```

Record for *plain data* (canonical shape, no optional fields, no rules);
builder for *shape and rules* (many optional fields, defaults, invariants,
hierarchies).

### Hierarchical builders — the senior pattern

The book's `Pizza` example (`GoodPizza`/`GoodNyPizza`/`GoodCalzone`): the base
builder is generic over its own type, `T extends Builder<T>`, with
`protected abstract T self()`. Each leaf returns `self()` so the fluent chain
keeps the most specific type:

```java
new GoodNyPizza.Builder(GoodNyPizza.Size.LARGE)
        .addTopping(Topping.HAM)
        .addTopping(Topping.ONION)      // still a NyPizza.Builder
        .build();                       // a GoodNyPizza
```

Forgetting either the `self()` override or the recursive bound breaks the
chain's type.

## Item 3 — Enforce the singleton property with a private constructor or an enum

A singleton = a class with exactly one instance. Three idioms:

1. **Public static final field** — simple, but callers see `INSTANCE`.
2. **Static factory** (`getInstance()`) — flexible: can become a
   non-singleton later, or a generic singleton, *without changing the API*.
3. **Enum type** — the *best* for a true singleton:
   - **Serialization-safe**: deserialization can't create a second instance.
     A field-based singleton that is `Serializable` produces a brand-new
     instance at deserialization unless it implements `readResolve()`
     (`BadSerializableSingleton` demonstrates the broken case; chapter 11
     covers `readResolve`).
   - **Reflection-safe**: the JVM forbids reflectively creating enum
     instances (`Constructor.newInstance` throws
     `IllegalArgumentException: Cannot reflectively create enum objects`),
     while a private constructor can be broken with `setAccessible(true)`
     (`BadSingleton` demonstrates the second instance).
   - One-line, and the JVM guarantees exactly one instance per enum constant.

**Senior gotcha**: if a field/factory singleton must be `Serializable`, add
`readResolve()` to preserve the singleton — and in modern Java, prefer the
enum anyway.

## Item 4 — Enforce noninstantiability with a private constructor

Utility classes (`Math`, `Arrays`, `Collections`) group static methods and
should never be instantiated. The trap: **if a class declares no constructor
at all, the compiler adds a public no-arg constructor by default** — so
`new BadUtilityClass()` "works" with zero effort and nobody can tell it was
an accident.

The remedy (`GoodUtilityClass`):

```java
public final class GoodUtilityClass {
    private GoodUtilityClass() {
        throw new AssertionError("No instances");
    }
}
```

- `final` — no subclassing either.
- `private` constructor — no external instantiation.
- `throw new AssertionError` — even an *internal* accidental instantiation
  (from another method of the class) fails loudly instead of silently
  existing.

## Item 5 — Prefer dependency injection to hardwiring resources

If a class *depends on an underlying resource* (a repository, a dictionary,
a calendar), don't hardwire it and don't fetch it from a singleton —
**pass it in**. Constructor injection has three wins:

- **Testability** — inject a fake/mock; you can verify behaviour without
  spinning up the real resource (`DependencyInjectionTest` injects a fake
  repository and asserts it was actually used).
- **Flexibility** — swap implementations by constructing with a different
  object; no code changes.
- **Explicitness** — the required resources are visible in the constructor
  signature, so nothing is hidden in global state.

Anti-patterns to recognise:

- **Hardwiring** (`BadHardwiredReportService`) — `new FileReportRepository()`
  inside the service: impossible to fake, impossible to swap.
- **Service locator** (`BadServiceLocatorReportService`) — a static registry
  that the service looks up: dependencies become invisible, and tests mutate
  *global* state, which leaks between tests. This is a step better than
  magic, but worse than DI: hidden, global, implicit.

Spring's constructor injection is exactly this idiom formalised.

## Item 6 — Avoid creating unnecessary objects

Reuse heavyweight or immutable objects instead of fabricating new ones:

- **Regex**: `String.matches(...)` recompiles the pattern on *every* call
  (`BadRegexMatcher`). Precompile once into a `static final Pattern`
  (`GoodRegexMatcher`).
- **Autoboxing**: `Long total = 0L; total += i;` unboxes and re-boxes on
  every iteration (`BadBoxedSum`). A primitive `long` runs much faster
  (`GoodPrimitiveSum`). The demo prints a timing comparison.
- **Literal strings**: `new String("x")` is pointless — `"x"` is already a
  shared instance. `StringBuilder` for a single `.append()` is pointless.
- Cheap throwaway objects: `LocalDate`-style immutable values, method-return
  temporaries — don't hoist these into fields unless the cost is real.

**The senior balance — don't overdo it.** Object *creation* is cheap on a
modern JIT; the expensive part is usually *retention* (see item 7) and
*allocation pressure*. So:

- Do reuse objects that are **expensive to construct** (regex patterns,
  `DateTimeFormatter`, pools of real connections).
- Don't build a hand-rolled pool for a *cheap* object to save allocations —
  the JIT escapes-analysis will remove many allocations anyway, and a pool
  adds complexity, bugs, and retention risks.
- Prefer primitives in hot loops; prefer immutable objects elsewhere.

## Item 7 — Eliminate obsolete object references

A *memory leak by reference*: the object is no longer needed, but something
still points to it, so the GC cannot reclaim it. This is **unintentional
object retention**.

The clearest case is a container that manages its own memory — like a stack.
`BadStack.pop()` moves the size down but leaves the popped element in the
backing array:

```java
return elements[--size];               // slot still references the popped object
```

`GoodStack.pop()` nulls the slot, so the object can be collected:

```java
Object result = elements[--size];
elements[size] = null;
return result;
```

The tests read the private `elements` array reflectively to *prove* the
difference without waiting for the GC: after `pop()`, the bad stack keeps
"b" in slot 1; the good stack has `null`.

**When to null out**: only when the class *manages its own memory* (owns the
array/collection that references stale objects), not everywhere — nulling
regular fields is clutter, not a fix.

**Where it bites in production**:

- **Caches** — entries are never evicted. If a cache outlives its values,
  clear expired entries or use `WeakHashMap` (`GoodWeakCache`) so values can
  be reclaimed when their keys die.
- **Listeners / callbacks** — an unsubscribe method must remove the
  listener; a forgotten removal keeps both listener and host alive forever.
- **Long-lived containers** (stacks, queues, pools) — empty slots must not
  pin large objects.

**How a senior finds these**: a heap dump with `jmap`/`MAT`/`JProfiler`;
look for many instances of a class that should have been garbage-collected,
then trace the farthest GC root back to the stale reference.

## Item 8 — Avoid finalizers and cleaners

- **`finalize()`** is deprecated (Java 9) and may be removed. Problems:
  there is **no guarantee it ever runs**, no guarantee of *when* (timing is
  unpredictable), it **degrades GC performance** (every finalizable object is
  tracked), and it can resurrect objects. In practice: never rely on it for
  resource cleanup.
- **`Cleaner`** (Java 9) — a callback invoked by GC when an object becomes
  unreachable. More predictable than `finalize`, but still **runs "eventually",
  at the GC's whim**, and the JVM may exit first. Regular `clean()` takes no
  argument; `clean()(Runnable)` (object registered) is deprecated.

The right pattern (`GoodAutoCloseableResource`):

```java
public final class GoodAutoCloseableResource implements AutoCloseable {
    public void use() {
        if (closed) throw new IllegalStateException("resource is closed");
    }
    @Override public void close() { closed = true; }
}
```

- Implement `AutoCloseable` and close **yourself** in a
  try-with-resources block (item 9).
- Guard `use()` so calling a closed resource fails loudly instead of acting
  on a dead resource.
- In the rare case a class touches *native* memory through a peer object,
  keep `Cleaner` as a **backstop** only — explicit closing stays the primary
  mechanism.

## Item 9 — Prefer try-with-resources to try-finally

Every resource that implements `AutoCloseable` should be closed with
try-with-resources, because try-finally is subtle and wrong-heavy:

**The masking bug.** With `try { read() } finally { close() }`, if `read()`
throws *and* `close()` also throws, the `close()` exception **propagates and
hides** the primary failure. The real error is lost. `TryWithResourcesTest`
proves it with a reader whose `read` throws "read boom" and whose `close`
throws "close boom":

- `BadTryFinallyFileReader`: the caller sees **"close boom"**, and the lost
  "read boom" cannot be recovered (`getSuppressed()` is empty).
- `GoodTryWithResourcesFileReader`: the caller sees **"read boom"** (the
  primary exception), and the close failure is attached as a *suppressed*
  exception — visible via `getSuppressed()`.

**Correctly closing several resources**:

```java
try (BufferedReader in = Files.newBufferedReader(path);
     FileWriter out = new FileWriter(dest)) {
    ...
}
```

Resources close in **reverse order of declaration**, each one is closed even
if an earlier one fails, and suppressed exceptions are preserved on the
primary.

**Senior checklist (items 3–9)**:

- [ ] Singleton: enum where possible; field/factory singleton that is
      `Serializable` must not break (enum, or add `readResolve`).
- [ ] Utility classes are `final` with a private ctor that throws.
- [ ] Dependencies pass through the constructor; no hardwiring, no hidden
      global registry lookups.
- [ ] Regex and formatter patterns compiled once; primitives in hot loops.
- [ ] Self-managed memory nulled out; caches and listener registries evict.
- [ ] Resources are `AutoCloseable`, closed in try-with-resources, with
      primary exceptions surviving close failures.

## Run it

```bash
mvn -q exec:java '-Dexec.mainClass=chapter2.demo.BuilderDemo'          # items 1-2
mvn -q exec:java '-Dexec.mainClass=chapter2.demo.ObjectLifecycleDemo'  # items 3-9
```

## Exercises

`exercises/chapter2.md` — tasks for items 1–9. Solutions in
`exercises/chapter2-solutions.md`.

## Further reading

- Effective Java items 1–9.
- Java 17 records (JEP 395) for the record-vs-builder decision.
- Chapter 10 (concurrency) expands the JavaBeans thread-safety point;
  chapter 11 covers serialization and `readResolve`.