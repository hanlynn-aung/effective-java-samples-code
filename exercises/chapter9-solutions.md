# Chapter 9 — Solutions (items 57–60)

## Solution 1 — Squeeze the scope (item 57)

```java
List<String> names = load();
int result = -1;
for (int i = 0; i < names.size(); i++) {
    String label = names.get(i);
    if (label.startsWith("X")) { result = i; break; }
}
// label dies here; result is the only value that must survive the loop.
```

`label` was the hazard — declared outside the loop, it was live and mutable
long before and after any use, inviting later code to read the last (maybe
stale) value. Now it's declared inside the loop where it's first produced. I
kept a single `result` (needed after the loop) but note a for-each with a
`break`, or a stream `indexWhere`, could often push even that down.

## Solution 2 — Kill the indexed loop (item 58)

```java
public long totalLengths(Iterable<String> words) {
    long total = 0;
    for (String word : words) {
        total += word.length();
    }
    return total;
}
```

An indexed loop requires `get(i)` and `size()` — neither exists on `Iterable`
(only `Iterable.iterator()`), so the method literally cannot be written with
indexing without first copying to a `List`. For-each works over any `Iterable`,
array, or `Collection`. You'd keep an indexed loop when you genuinely need the
index itself — e.g. pairing elements by position, or a lookback to `i-1` (and
even then a for with a bi-indexed double pass is usually the better read).

## Solution 3 — Reach for the library (item 59)

`hashFirstChar` → `Character.toLowerCase(s.charAt(0))`/`Character.toUpperCase`
(or `String.toLowerCase()`), not a hand-rolled case shift; for counting
occurrences, `String.indexOf(sub, from)` in a loop or `Collections.frequency`
on a `List<Character>`, but the idiomatic one is:

```java
// java.lang: non-reinvented single-hash
static int hashFirstChar(String s) { return s.charAt(0); } // a char is already the value
// search utility
static int countMatches(String s, String sub) {
    int n = 0, i = s.indexOf(sub);
    while (i >= 0) { n++; i = s.indexOf(sub, i + sub.length()); }
    return n;
}
```

Failure modes a hand-rolled version risks: miscounting overlapping matches,
wrong case handling/locale, off-by-one at string end, and `null`/empty input.
The library version has had all these reviewed for you.

## Solution 4 — Exact money (item 60)

`19.99 * 3 * 0.0725` in `double` yields a value like
`4.3478249999999996` — not the exact `4.347825`, because `19.99`, `3`, and
`0.0725` are all non-terminating binary fractions. Each multiply drifts; a
statement that should be `4.35` comes out `4.35...` off.

```java
import java.math.BigDecimal;
import static java.math.RoundingMode.HALF_UP;

BigDecimal price = new BigDecimal("19.99");
BigDecimal qty   = new BigDecimal("3");
BigDecimal rate  = new BigDecimal("0.0725");
BigDecimal tax   = price.multiply(qty).multiply(rate)
                       .setScale(2, HALF_UP);      // 4.35
```

Two exact alternatives: **integer minor units** (`long cents = 1999L; tax =
1999L * 3 * 725 / 100_000...` with care), appropriate for many APIs; or
**`long`/`int` with an explicit scale** where you control rounding yourself.
Both avoid the binary floating-point drift entirely.
