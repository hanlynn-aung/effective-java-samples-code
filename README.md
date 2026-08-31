# Effective Java — Mid → Senior Study Path

A self-study curriculum built from *Effective Java* (Joshua Bloch, 3rd ed.).
Each chapter pairs a deliberately problematic `Bad` example with a preferred
`Good` example, and adds the things a senior must actually know: deep-dive
reasoning, runnable demos, JUnit-verified behaviour, and exercises with
solutions.

This is a learning repository. Work through chapters in order, read the
deep-dive, run the demo and the tests, then do the exercises before checking
the solutions.

## Layout

- `src/main/java/chapterN/{bad,good}/` — example sources, split into
  `Bad` (anti-pattern, still compilable) and `Good` (preferred approach).
- `src/main/java/chapterN/demo/` — runnable `main()` demos that show the
  `Bad` vs `Good` difference at runtime.
- `src/test/java/...` — JUnit 5 tests that pin the `Good` behaviour and
  expose the `Bad` behaviour.
- `chapterN/README.md` — deep-dive explanation for that chapter.
- `exercises/chapterN.md` + `exercises/chapterN-solutions.md` — practice tasks.

## Prerequisites

- Java 17+
- Maven 3.9+

## Build and verify

```bash
mvn clean test          # compile everything and run all JUnit tests
```

## Run a demo

```bash
mvn -q exec:java -Dexec.mainClass=chapter1.demo.ConnectionFactoryDemo
```

On Windows PowerShell, quote the mainClass argument:
`mvn -q exec:java '-Dexec.mainClass=chapter1.demo.ConnectionFactoryDemo'`

(Every chapter has at least one `chapterN.demo.*Demo`.)

## Study workflow

1. Read `chapterN/README.md` — understand the concept and the senior gotchas.
2. Run the chapter's demo — watch the `Bad` mishandle and the `Good` hold up.
3. Read the tests — they encode the contract: what `Good` guarantees and what
   `Bad` lets slip.
4. Do `exercises/chapterN.md` without looking, then check the solutions.

## Curriculum

| Chapter | Topic | What a senior learns |
|---------|-------|----------------------|
| 1 | Static factory methods (item 1) | Named construction, instance control / caching, returning subtypes, when the constructor is still right |
| 2 | Creating & destroying objects (items 1–9) | Builders, singletons, noninstantiability, DI, avoiding needless objects, obsolete references, try-with-resources |
| 3 | Immutability & defensive copying | Immutable design, protecting internal state, `record` |
| 4 | Composition over inheritance (item 18) | Fragile inheritance, delegation wrappers |
| 5 | Generics & type safety (items 26–33) | Raw types, wildcards, PECS, bounded type parameters |
| 6 | Enums & annotations (items 34–41) | Enums with behaviour, `EnumSet`/`EnumMap`, strategy enum |
| 7 | Lambdas & streams (items 42–48) | Method references, side-effect-free streams, parallel safety |
| 8 | Methods & general programming (items 49–68) | Parameter validation, defensive copies, `BigDecimal` for money |
| 9 | Exceptions (items 69–77) | Checked vs unchecked, exception chaining, never swallowing |
| 10 | Concurrency (items 78–84) | Atomicity, `synchronized` vs atomics, `volatile`, executors, `CompletableFuture` |
| 11 | Serialization (items 85–90) | Prefer alternatives, safe deserialisation, `transient` secrets |
| 12 | API design | Small clear APIs, Javadoc, safe collection returns |