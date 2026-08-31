# Chapter 7 — Lambdas and Streams (items 42–48)

Round 1 covers the *lambda + functional-interface* items (42–44); the stream
items (45–48) continue below.

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

## Senior checklist (round 1)

- [ ] Function values are lambdas unless they need state/`this`/multiple methods (42).
- [ ] Prefer a method reference whenever one already states the body's intent (43).
- [ ] Custom single-method interfaces are almost always a standard
      `java.util.function` type instead (44).

## Exercises

`exercises/chapter7.md` then check `exercises/chapter7-solutions.md`.

## Verify

```bash
mvn -q clean test
mvn -q exec:java '-Dexec.mainClass=chapter7.demo.LambdaFunctionsDemo'
```