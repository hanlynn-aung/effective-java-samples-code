# Chapter 9 — Exercises (items 57–60)

## Exercise 1 — Squeeze the scope (item 57)

```java
int result;
String label;
List<String> names = load();
for (int i = 0; i < names.size(); i++) {
    label = names.get(i);
    if (label.startsWith("X")) { result = i; break; }
}
```

Rewrite so every variable is declared where it is first used, in the smallest
possible scope, and state which existing variable was the scope hazard.

## Exercise 2 — Kill the indexed loop (item 58)

```java
public long totalLengths(Iterable<String> words) {
    long total = 0;
    // currently needs an index... but Iterable has no get()!
    return total;
}
```

Write the method with a for-each loop and explain why this could not be done
with indexing — and name one real case where you *would* keep an indexed loop.

## Exercise 3 — Reach for the library (item 59)

```java
public static String hashFirstChar(String s) { /* hand-rolled */ }
public static int countMatches(String s, String sub) { /* hand-rolled */ }
```

Name the JDK methods that already do each job correctly (one from
`java.lang`, one search/array utility), and give two concrete failure modes a
hand-rolled version could get wrong.

## Exercise 4 — Exact money (item 60)

```java
double price = 19.99;
double qty = 3;
double tax = price * qty * 0.0725;
```

Show the exact `double` value of `tax` (or the drift class of error), then
rewrite using `BigDecimal` built from `String`, rounding to 2 decimals with
`HALF_UP` at the end. State two alternatives to `BigDecimal` that are also
exact.
