# Chapter 7 — Solutions (items 42–44)

## Solution 1 — De-anonymize (item 42)

```java
Executor exec = () -> System.out.println(task);
```

`this` inside the anonymous class is the anonymous *class instance* (the
`Executor` object being created). `this` inside the lambda is the **enclosing
instance** — the object that lexically surrounded the lambda.

Where it matters: serialization, and any code that does `this.getClass()` or
captures `this` for a nested call. If the anonymous class needed to call a
*captured* enclosing method through the outer `this`, and a lambda wanted the
same thing, the two resolve differently — a classic trap is a lambda introducing
`this.method()` on the enclosing class when the anonymous class would have
referred to itself. State-holding and multi-method behavior are the two cases
that still need an anonymous class.

## Solution 2 — Concurrency of references (item 43)

1. `String::toUpperCase` → `(String s) -> s.toUpperCase()` — **unbound instance**
   (method invoked on the argument).
2. `LocalDate.now()::isAfter` → `(LocalDate d) -> LocalDate.now().isAfter(d)` —
   **bound instance** (the receiver `LocalDate.now()` is captured up front).
3. `Math::max` → `(int a, int b) -> Math.max(a, b)` — **static**.
4. `Integer[]::new` → `(int len) -> new Integer[len]` — **constructor**.
5. `this::toString` → `() -> this.toString()` — **bound instance** (receiver is
   `this`, captured).

## Solution 3 — Map the callback to a standard interface (item 44)

`void schedule(int delayMs, TimerTask task)` is a two-parameter, void-returned
callback — that's `BiConsumer<Integer, TimerTask>` (an `IntConsumer`-style
specialization doesn't exist with a second arg, so `BiConsumer` is the fit, or
`ObjIntConsumer<TimerTask>` for the box-avoiding form). A custom interface would
be justified when you want to attach *stronger contract meaning* (documented
preconditions, a reuse contract, or extra default methods) rather than a family
of lambdas, or when you're matching a legacy `Timer`-style API that demands a
nominal type.

## Solution 4 — The four standard slots (item 44)

- `Supplier<T>` — produces a value with no input (e.g. lazy config loader).
- `Consumer<T>` — consumes one value, no output (e.g. logging a line).
- `Function<T,R>` — maps one value to another (e.g. extracting a field).
- `Predicate<T>` — tests one value returning boolean (e.g. a filter).

```java
Predicate<String> nonBlank = String::isBlank.negate();      // or s -> !s.isBlank()
Function<Integer, String> roman = this::toRoman;            // bound instance ref
```

`nonBlank` here is a method reference (`String::isBlank`) combined with
`negate()`; `roman` binds `this.toRoman` — both exercising item 43's refs
inside item 44's standard types.
