# Chapter 2 — Creating and Destroying Objects

Each item is split into `Bad` and `Good` packages. Source lives in `chapter2/itemN/{bad,good}` with package `chapter2.itemN.{bad,good}`.

- `item1` — Consider static factory methods instead of constructors
- `item2` — Consider a builder when faced with many constructor parameters
- `item3` — Enforce the singleton property with a private constructor or an enum type
- `item4` — Enforce noninstantiability with a private constructor
- `item5` — Prefer dependency injection to hardwiring resources
- `item6` — Avoid creating unnecessary objects
- `item7` — Eliminate obsolete object references
- `item8` — Avoid finalizers and cleaners
- `item9` — Prefer try-with-resources to try-finally

The bad examples demonstrate a design problem; the good examples show the preferred approach.

Build all examples from the repository root with:

```bash
mvn clean test
```
