# Chapter 6 — Exercises (items 34–38)

Work through these before reading the solutions.

## Exercise 1 — Convert a magic-number table (item 34)

Given:

```java
int MONDAY = 1, TUESDAY = 2 /* ... SUNDAY = 7 */;
static String dayName(int day) { switch (day) { case 1: return "Mon"; /* ... */ } }
```

Rewrite `dayName`/the constants as an enum. Why does `dayName(99)` — which
today returns `null` — become impossible to call wrongly? Also show how `values()`
replaces the hand-maintained list used for "print all days".

## Exercise 2 — Hunt the ordinal corruption (item 35)

```java
enum Severity { LOW, HIGH, MEDIUM;   // someone moved MEDIUM up
    int weight() { return ordinal(); }
}
```

Write down the *runtime values* of `LOW.weight()`, `MEDIUM.weight()`, and
`HIGH.weight()` as this code exists, then rewrite `Severity` so the "weights"
LOW=1, MEDIUM=2, HIGH=3 are correct regardless of how the constants are
reordered, with no risk of silent drift.

## Exercise 3 — Bit-field to EnumSet (item 36)

```java
int F = 1<<0, B = 1<<1, U = 1<<2;
String render(int s) { return (s & F)!=0 ? "f" : "" + ((s & B)!=0 ? "b" : "") ... }
```

Write the `EnumSet<Style>` equivalent of `render`, and give two reasons the
enum version is strictly safer than the int mask version (think about what
happens with `render(0)` and `render(1<<20)`).

## Exercise 4 — Ordinal indexing audit (item 37)

```java
Size[] sizes = new Size[Size.values().length];
sizes[someSize.ordinal()] = someSize;
```

Explain two concrete ways this pattern can produce a wrong or `null` bucket
without any exception. Then sketch an `EnumMap<Size, List<Item>>` replacement
that guarantees one key-set entry per enum value.

## Exercise 5 — Emulate extension (item 38)

Write a minimal `interface Ranged` with a `double range(double)` method and two
enums `BasicRange` (`CLAMP`, `WRAP`) implementing it. Then write one method
`applyRange(Ranged r, double v)` that works for *either* enum, and note where
this idiom fails (what an enum can still NOT provide via an interface).

## Exercise 6 — Naming-pattern relapse (item 39)

A legacy test suite runs methods whose names start with `testISay`. Two silent
losses occur: a method renamed `testxISay` and one named `tetsTheRealOne`.
Explain why neither is caught by any tool, then design a `@Retention(RUNTIME)`
`@Target(METHOD)` annotation plus a tiny runner (in prose/pseudocode) that
reports passes *and* the exception of failing tests instead of skipping them.

## Exercise 7 — The missing @Override (item 40)

```java
class Node { public String value() { return "node"; } }
class Leaf extends Node { public String getValue() { return "leaf"; } }
```

A `Leaf` stored as a `Node` returns `"node"`. Explain exactly why (what `@Override`
would have done), and write the corrected `Leaf`.

## Exercise 8 — Marker interface vs annotation (item 41)

Design a method `List<T> findByX(Object query)` either as `findByX(GoodQueryable)`
or `findByX(Object)` + an `@Queryable` check. Recommend which and justify using
*both* `instanceof`-type-safety and compile-time enforcement. When (give one
concrete case) is an annotation the better marker instead?