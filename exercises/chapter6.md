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