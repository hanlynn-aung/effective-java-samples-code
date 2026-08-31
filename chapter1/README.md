# Chapter 1 — Consider Static Factory Methods Instead of Constructors

**Effective Java, Item 1.** Theme: *object creation*.

> The traditional way to get an instance is a public constructor. The
> static factory method — a static method that returns an instance — is often
> the better choice.

## Learning objectives

After this chapter you should be able to explain, from memory:

1. The **four benefits** of static factories over public constructors.
2. The **two downsides**, and when a constructor is still the right tool.
3. What **instance control** means and why caching changes identity semantics.
4. Why a static factory can **return a subtype** and hide the implementation.
5. The **naming conventions** (`of`, `valueOf`, `from`, `to`, `create`,
   `newInstance`, `getInstance`, `getType`, `newType`).

## Concept deep-dive

A *public constructor* is literally the only way to force a caller through
your class's Chapter: `new Thing()`. A *static factory method* is a plain
static method that returns an instance:

```java
public static Thing withName(String name) { ... }
```

Why prefer the factory over the constructor?

**Benefit 1 — it has a name.** `new Point(double, double)` tells you nothing;
`Point.cartesian(x, y)` and `Point.polar(r, theta)` tell you everything. When
a class needs several ways to be created, factory names document the
difference. Readability scales to the whole codebase: call sites become
self-explanatory.

**Benefit 2 — instance control.** The factory does not have to create a new
object. It can return a **cached** instance, a **singleton**, or the **same
instance** for equal inputs. Constructors are forced to create fresh objects.
Instance control is exactly what makes singletons, flyweights and the
`Integer.valueOf`/`Boolean.valueOf` caches possible. It also lets you write
classes with *only* static methods (utility + instance-controlled).
Caveat: caching immutable objects is safe; caching *mutable* ones leads to
aliasing bugs, so the same-instance guarantee only works for immutable
objects.

**Benefit 3 — you can return any subtype.** The factory's declared return
type can be a supertype or interface; the concrete class can be private
inside the implementing class. Callers depend on the abstraction, never the
implementation. This is the foundation of *service-provider frameworks*:
callers ask the factory for a service and get whatever implementation is
configured. You cannot hide the implementation behind a constructor — callers
must know the exact class to `new` it.

**Benefit 4 — the returned type can vary by input or evolve over time.**
The same factory can return different implementations depending on its
arguments (e.g. `Connection.secure(address)` vs `Connection.plain(address)`).
Because callers only see the declared return type, a later version can add a
new implementation class without breaking any caller — a kind of API
evolution you do not get from `new` at the call site.

**Downside 1 — hard to discover.** A constructor is obvious; a factory is
just another static method. Conventions help: `from` (single-arg conversion),
`of` (multi-arg aggregation), `valueOf` (loose-typed conversion), `instance`/
`getInstance` (instance control), `create`/`newInstance`, `getType`.
Prefer the factory when it names the how/why and the constructor is
ambiguous; prefer a plain constructor when the class has one obvious,
self-named construction.

**Downside 2 — no subclassing without a public/protected constructor.**
This is usually a *feature* (see chapter 4), but if you design for extension,
a hidden constructor blocks it. The factory composes internally; the class
cannot be `extends`ed by outsiders.

**Senior-level footnote — platform APIs use factories everywhere you've
already relied on:**

- `Boolean.valueOf(boolean)` returns one of two cached instances — identity
  is guaranteed.
- `List.of(...)`, `Map.of(...)` return compact immutable collections.
- `LocalDate.of(...)`, `Period.between(...)`.
- `Optional.of(x)` / `Optional.ofNullable(x)` / `Optional.empty()` —
  instance control for the empty singleton.
- `Collections.unmodifiableList(...)` creates a *view*, not a copy.
- `String.format` is a factory for formatted strings; `String.valueOf`.

That second point is a trap for seniors too: `Integer.valueOf(127) ==
Integer.valueOf(127)` is `true` (cached), but the same comparison at `128` is
`false`. *Never compare boxed instances with `==`; the cache is an
implementation detail you must not rely on for anything other than
`Boolean`.*

## The examples in this chapter

Core pair (already present):

- `BadConnectionFactory` — public constructor on a public class; no
  validation, callers can construct anything, every call is a fresh object.
- `GoodConnectionFactory` — private constructor + named static factory `to`,
  with `Objects.requireNonNull` validation.

Extended examples (the two benefits constructors cannot give you):

- `GoodCachedConnections.to(String)` — **instance control**. Repeated calls
  with the same address return the *same* immutable connection (cached in a
  `ConcurrentHashMap`). Try that with a constructor: impossible.
- `GoodTypedConnections.plain/secure` — **returning subtypes**. The public
  `Connection` interface hides two implementation classes; callers can never
  construct or depend on a concrete type, and new implementations can be
  added without breaking anyone.

## Bad walkthrough

```java
Connection c1 = new BadConnectionFactory().open("db://x");
Connection c2 = new BadConnectionFactory().open("db://x");  // unrelated object
```

- No validation: `open(null)` succeeds and silently produces a broken
  connection — the failure shows up later, far from the cause.
- No names: constructing `new Connection(address)` tells the reader nothing
  about meaning, and you cannot have two differently-named constructions.
- No instance control: every call is a new object even when identity is
  irrelevant and reuse would be cheaper.
- No type hiding: callers must know `Connection` itself is concrete.

## Good walkthrough

```java
Connection c = GoodConnectionFactory.open("db://x");   // → Connection.to(...)
```

- Fail-fast: `Objects.requireNonNull` rejects `null` immediately.
- Named: `Connection.to(address)` reads like an instruction, not plumbing.
- Private constructor: nobody bypasses validation.
- `GoodCachedConnections.to(...)` reuses instances; `GoodTypedConnections`
  hands back the implementation it considers right for the arguments.

## Senior checklist

- [ ] Every public construction point has a *name* that means something.
- [ ] `null` is rejected at the boundary (`Objects.requireNonNull`), not at
      first use.
- [ ] If instances are cheap and identity-unimportant, prefer reuse where it
      matters (immutable shared instances, pools) and `new` elsewhere — do
      not over-engineer a cache.
- [ ] Callers never see a concrete implementation class they did not choose.
- [ ] Never returned `null` from a factory — return `Optional` or throw
      (item 54).
- [ ] Spring/DI note: "static factory" here is a *language* idiom — the
      framework equivalent is a `@Bean` factory method or provider. The
      principles (named, instance-controlled, subtype-returning) transfer.

## Run it

```bash
mvn -q exec:java -Dexec.mainClass=chapter1.demo.ConnectionFactoryDemo
```

## Exercises

`exercises/chapter1.md` — six tasks (concept questions + a rewrite). Solutions
in `exercises/chapter1-solutions.md`.

## Further reading

- Effective Java, item 1 (this chapter), items 2–4 (next chapter).
- `java.util.function.Function`-style fluent factories, e.g. `Stream.of`.
- Item 54 ("return empty collections or optionals, not null") pairs with the
  "never return null" rule.