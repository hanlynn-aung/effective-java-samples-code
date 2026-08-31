# Chapter 7 — Lambdas and Streams (items 42–48)

All eight items are now built. Items 42–44 cover lambdas and functional
interfaces; items 45–48 cover streams.

| Item | Title | Core idea |
|------|-------|-----------|
| 42 | Prefer lambdas to anonymous classes | A lambda is the idiomatic, state-free one-expression function |
| 43 | Prefer method references to lambdas | When a method already does the job, name it — shorter and clearer |
| 44 | Use standard functional interfaces | Don't invent a one-method interface `java.util.function` already has |
| 45 | Use streams judiciously | A stream is a tool, not a badge; don't contort code to fit one |
| 46 | Prefer side-effect-free functions | Streams are pipelines — keep them pure, collect via `Collectors` |
| 47 | Prefer `Collection` to `Stream` as a return type | Callers want to re-iterate; return a `Collection` |
| 48 | Caution with parallel streams | Parallelism is only safe for stateless, order-independent work |

---

## Item 42 — Prefer lambdas to anonymous classes

Historically, "behavior as a value" meant an anonymous class whose single method
you override. In Java 8+ the same function object is a **lambda** — one
expression instead of a noisy anonymous class, and crucially with cleaner
semantics:

- **No accidental `this`.** Inside an anonymous class, `this` is the *class
  itself*; inside a lambda, `this` is the *enclosing* instance. A lambda can't
  accidentally capture the wrong receiver.
- **No accidental state.** An anonymous class can hold fields; a lambda cannot
  — it only captures effectively-final locals. That state-lessness is exactly
  what makes functions safe to pass around (and helps item 48).
- **Concise.** `value -> value.trim()` beats a full anonymous class every time.

When is an anonymous **class** still right? When you need *state*, or more than
one method, or `this` meaning "the object itself" (e.g. `this::method` inside
it). Otherwise: lambda.

**Sample:** the existing `BadFormatter` wraps `Function` in a 10-line anonymous
class; `GoodFormatter` is a one-line lambda. `LambdaTest` asserts both produce
identical output and shows the lambda holds no accidental state.

---

## Item 43 — Prefer method references to lambdas

The most concise lambda is often not a lambda at all. If a class already has a
method that does exactly what the lambda body does, use a **method reference**:
shorter, and it names the invoked behavior instead of re-describing it.

The four reference shapes every senior recognizes instantly:

| Shape | Example | Meaning |
|-------|---------|---------|
| Static | `Integer::parseInt` | call the static method |
| Bound instance | `"bam"::equals` | call an instance method on a captured object |
| Unbound instance | `String::length` | call an instance method on the (first) arg |
| Constructor | `ArrayList::new` | `new ArrayList` |

**Sample:** `GoodConcise` uses `String::length` in a comparator, a bound/static
reference, and `ArrayList::new` in a collector; `BadVerbose` re-types the same
logic as raw lambdas. `MethodReferenceTest` asserts the reference and its
equivalent lambda agree (they must, by definition) and that both produce the
same counting result.

**Caution:** don't force it. A method reference with several casts or a
constructor that reads poorly (e.g. `TreeMap<K,V>::new` from a map-supplier
slot) is worse than a clear lambda. Clarity wins — a method reference that
hurts clarity is a lambda in disguise.

---

## Item 44 — Favor the use of standard functional interfaces

`java.util.function` ships the 43 functional interfaces that cover nearly every
callback shape Java needs. Before you declare a single-method interface, ask
"which of the standard 43 is this?":

- `Runnable`, `Supplier<T>`, `Consumer<T>`, `Function<T,R>`, `Predicate<T>`,
  `UnaryOperator<T>`, `BinaryOperator<T>` and their `Bi*`, `Int*`, `Long*`,
  `Double*` primitives.

Rolling your own duplicates the type and, worse, stops your API from accepting
a plain lambda or a method reference the standard shape already accepts. Keep
your custom interface only when it adds contractual meaning (like `Comparator`,
which has more explicit contracts than `ToIntBiFunction`).

**Sample:** `BadInventedEvent` declares a bespoke `@FunctionalInterface
PriceListener { void onPrice(double); }` — that's `DoubleConsumer` (or, with a
label, a `BiConsumer`). `GoodUseStandard` paramaterizes against `BiConsumer`,
`Predicate`, `Function`, and `Supplier` so callers can pass raw lambdas and
method references with no dependency on a custom type. `FunctionalInterfaceTest`
shows the standard forms working directly.

---

## Item 45 — Use streams judiciously

A stream pipeline can be as opaque as it is clever. The book's advice: streams
shine for **uniform transformations over a homogeneous sequence** — filter,
map, reduce, collect — and over some loop-heavy, nested-index code. They're the
wrong tool the moment the code needs to read a file and return early, needs to
see neighbours, or is clearer as a small imperative loop.

The senior discipline: **if the stream is denser than the loop it replaces,
it's not better.** Prefer readability; split a long chain into named
intermediate variables; and where an indexed `for` reads plainly, use it.

**Sample:** `BadStreamSpaghetti.letterCountsAcrossLines` squeezes a 20-line
job into one dense `flatMap→filter→map→map→collect` chain.
`GoodClearStreams.letterCounts` is a plain nested loop using `merge`. The test
proves both count the *identical* letters — the improvement is purely clarity,
which is the whole point.

---

## Item 46 — Prefer side-effect-free functions in streams

A stream's functions should be **pure**: no shared mutable state, no writing
outside the pipeline. The biggest red flag is using `forEach` to stuff results
into an *external* collection:

```java
List<String> bucket = new ArrayList<>();
stream.forEach(w -> bucket.add(w.toUpperCase()));   // side effect, order-dependent
```

This mutation breaks stream reuse, makes the pipeline dependent on encounter
order, and is **unsafe in parallel** — multiple threads writing the same list
race. The pure equivalent collects *inside* the pipeline:

```java
List<String> result = stream.map(String::toUpperCase).collect(Collectors.toList());
```

If you genuinely need side effects (logging, I/O), do it deliberately in a
`forEach` whose purpose is the side effect — and keep it sequential.

**Sample:** `BadStatefulCollect` uses a side-effecting `forEach` into an
external `ArrayList`; `GoodCollectors` maps and `collect(Collectors.toList())`
purely. `SideEffectFreeTest` shows both produce the same result — but only the
good version is order-independent and parallel-safe.

---

## Item 47 — Prefer `Collection` to `Stream` as a return type

`Stream<T>` has no `size()`, no `get(i)`, and — critically — is **a single-use,
consumable value**. Once you `.count()` or `.collect()` it, it's gone; a second
read throws `IllegalStateException`.

An API method that returns data the caller might want to inspect more than once,
pass around, or keep, should return a `Collection` (typically `List`/`Set`, or a
lazy but re-iterable type for huge data). Make the *operation* stream-based
internally, but hand back a concrete, re-iterable collection. Stream explicitly
only when the caller is committed to a one-shot pipeline (huge/infinite data,
monadic pipelines).

**Sample:** `BadStreamOnlyApi` hands out a one-shot `Stream<Tuple>`; reading it
twice throws. `GoodCollectionApi` returns an immutable `Collection<Tuple>` and
provides a separate `recentScoresStream()` for callers who want to stream.
`CollectionReturnTest` shows the collection re-iterated and queried by `size()`
while the stream throws on its second pass.

---

## Item 48 — Use caution when making streams parallel

`parallelStream()` looks like free speed, but it's only correct when every
intermediate operation is **pure, stateless, and lifting the assumptions of
shared mutable state and encounter order**. Two rules dominate:

1. **Never parallelize side-effecting code.** Item 46's
   `forEach`-into-a-shared-list becomes a data race under parallelism.
2. **Use associative reductions.** A parallel pipeline splits the work, so
   `reduce`/`sum` must be associative (`(a+b)+c == a+(b+c)`) — then the
   framework combines sub-results safely and deterministically.

Even then there's no guarantee a parallel stream is faster — the fork/join
overhead can dwarf the savings on small or ordered pipelines. Measure first.

**Sample:** `BadParallelSum.badParallelSum` runs `parallelStream().forEach(v ->
total[0] += v)` into a shared `long[]`. `ParallelCautionTest` shows the 
sequential baseline is exact (`499999500000`) while the parallel mutation is
racy — the demo we ran returned `142288531317`. `GoodParallelReduce.sum` uses
`parallel().asLongStream().sum()`, an associative reduce that stays exactly
`499999500000` every run.

---

## Senior checklist

- [ ] Function values are lambdas unless they need state/`this`/multiple methods (42).
- [ ] Prefer a method reference whenever one already states the body's intent (43).
- [ ] Custom single-method interfaces are almost always a standard
      `java.util.function` type instead (44).
- [ ] Streams for uniform transformations; a plain loop where it reads better, and
      never force a pipeline (45).
- [ ] Stream functions are pure; collect via `Collectors`, never `forEach` into
      external state (46).
- [ ] Methods returning small, re-iterable data return `Collection`, not
      one-shot `Stream` (47).
- [ ] Parallel streams only on stateless, associative-reduction pipelines, and
      only after measuring (48).

## Exercises

`exercises/chapter7.md` then check `exercises/chapter7-solutions.md`.

## Verify

```bash
mvn -q clean test
mvn -q exec:java '-Dexec.mainClass=chapter7.demo.LambdaFunctionsDemo'
```