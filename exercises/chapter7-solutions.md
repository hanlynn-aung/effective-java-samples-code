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

## Solution 5 — Stream or loop? (item 45)

Stream pipeline:

```java
Map<LocalDate, Long> daily = transactions.stream().collect(
    groupingBy(Transaction::getDate, summingLong(Transaction::getAmount)));
```

Plain loop:

```java
Map<LocalDate, Long> daily = new HashMap<>();
for (Transaction t : transactions) {
    daily.merge(t.getDate(), t.getAmount(), Long::sum);
}
```

I'd ship the **stream** version here: the data is a homogeneous list, the
operation is a clean transform→aggregate with no branching, and
`groupingBy(..., summingLong(...))` expresses "by date, sum amounts" in one
line. The loop is only equally clear at this small size; streams' real win is
parallelizability (see item 48) and less mutable ceremony. Note it's *not* a
given — for stateful, early-exit, or neighbour-access logic the loop wins.

## Solution 6 — Purify it (item 46)

```java
Map<String, Integer> totals = transactions.stream().collect(
    Collectors.toMap(Transaction::getName, Transaction::getAmount, Integer::sum));
```

The original is order-dependent because `merge` mutates a single external
`totals` map in encounter order — reverse the stream and you still get the same
sum (since addition is commutative), but the *impurity* is the problem: nothing
in the stream owns the shared map, and the moment you call `.parallelStream()`,
multiple threads call `totals.merge` concurrently — a data race on a
non-thread-safe `HashMap`. `Collectors.toMap` (or `groupingBy`) collects *into*
the result inside the pipeline, so it's pure, order-independent, and parallel-safe.

## Solution 7 — Return type judgment (item 47)

`Collection<String>` serves the caller better. It can be `size()`d, iterated
repeatedly (print count, then print words, then pass along), and stored with no
consumption. `Stream<String>` is one-shot: after counting you can't print the
same stream again — a second `count()` throws `IllegalStateException`, and you
can't hand it to a method that needs to read it more than once. So the API
should compute inside but return a `Collection` (the book's preference), saving
an explicit `stream()` for callers who really want a pipeline.

## Solution 8 — Parallel audit (item 48)

Two independent reasons it's unsafe:

1. **Shared mutable state / data race.** All threads run
   `cache.put(...)` on the same unsynchronized `HashMap`. Concurrent writes
   with no happens-before edge can corrupt the map's buckets (and in pre-8
   HashMap, cause infinite loops); there's no ordering guarantee on the result.
2. **Side effect inside the pipeline.** Item 46's rule — `forEach` mutating
   external state breaks the pipeline's purity, so parallelizing it is
   meaningless (garbage) rather than merely slow.

Safe rewrite — parallel with an associative, pure reduction:

```java
// threadsafe + associative: parallelize the reduction, not the mutation
Map<Integer, Long> counts = employees.parallelStream().collect(
    Collectors.groupingBy(Employee::id, Collectors.counting()));
// or, mutating a ConcurrentHashMap per key is still shared-state; prefer the reducer.
```

The rule: keep functions pure and let the pipeline collect; only then is
parallelism a correctness-neutral (if non-guaranteed-speed) choice.
