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

## Solution 5 — Spot the boxed trap (item 61)

```java
Map<String, Integer> ages = new HashMap<>();
ages.put("a", 25);
ages.put("b", null);
boolean ok = ages.get("a") != ages.get("b");   // true (25-box vs null: different refs)
int sum = ages.get("a") + ages.get("b");        // NPE: unboxing age("b") == null
```

- `ages.get("a") != ages.get("b")` compares **references**: the box for `25`
  vs the `null` reference → `true`, but that's identity, not value — and it
  would misbehave for two equal non-cached numbers too.
- `int sum = ... + ...` **unboxes** both; `ages.get("b")` is `null`, and
  unboxing `null` throws `NullPointerException`.

Rewrite handling `null` explicitly, using primitives for the math:

```java
Integer a = ages.getOrDefault("a", 0);
Integer b = ages.getOrDefault("b", 0);
int sum = a + b;                       // null became 0 before any unboxing
boolean ok = a != null && b != null;   // compare boxes only with .equals or after unbox
```

Better: since primitive `int` can't be `null`, prefer `Map<String,Integer>` +
`getOrDefault` with an explicit missing-value policy, keeping the arithmetic on
primitive `int`.

## Solution 6 — Kill the magic strings (item 62)

```java
public enum Status { NEW, OPEN, CLOSED }

private Status status;
public void setStatus(Status status) {   // parameter typed, no runtime check needed
    this.status = status;
}
public boolean isOpen() { return status == Status.OPEN; }
```

Compile-time guarantees gained:
1. **No invalid value** — `setStatus(Status.NEW)`; a bogus `"FROZEN"` or a
   casing typo (`"open"`) simply doesn't compile.
2. **Value matching is atomic `==`**, not string `.equals` gymnastics — with
   exhaustive `switch` possible.

Behaviour the enum can attach that strings can't: each constant may carry
fields/methods (item 34), e.g.
`OPEN` with `boolean readOnly() { return false; }` or a `next()` transition, so
state logic lives *on* the type instead of scattered `if status.equals(...)`
chains.

## Solution 7 — Repair the joiner (item 63)

```java
public String csv(Iterable<String> rows) {
    StringBuilder out = new StringBuilder();
    boolean first = true;
    for (String row : rows) {
        if (!first) out.append(',');
        out.append(row);
        first = false;
    }
    return out.toString();
}
```

`out = out + "," + row` is O(n²): each `+` copies the whole accumulated string.
`StringBuilder.append` amortizes to O(n). Give it capacity if you know it
(`new StringBuilder(totalEstimate)`), so it doesn't grow/copy. For a plain
delimited join of strings, prefer the one-liner `String.join(",", rows)` — the
library already implements exactly this, correctly and fast (item 59).

## Solution 8 — Interface over implementation (item 64)

```java
public class Cache {
    private final Map<String, Entry> store = new HashMap<>();
    public Map<String, Entry> entries() { return store; }
}
```

Critique: the original pinned **both** the field and the return type to
`HashMap`:
- callers of `entries()` receive a `HashMap`, so anything that mutates/reads it
  is coupled to that exact class — switching to `ConcurrentHashMap` or
  `LinkedHashMap` later breaks them.
- changing the field type is a **source+bytecode signature change**, so it can't
  be swapped internally without breaking the API.

Interfaces fix it: field is `Map`, initialized to a concrete `HashMap` but
replaceable; return type is `Map<String,Entry>`. To avoid exposing the mutable
internal at all, return a modifiable copy or an unmodifiable view
(`Map.copyOf` / `Collections.unmodifiableMap`) — tie into item 50. Example
swaps a caller could make: `Map` → `ConcurrentHashMap` for the multi-threaded
case or `TreeMap` for ordered iteration; a `List` field → `ArrayList`,
`LinkedList`, `CopyOnWriteArrayList`, or `List.of()/List.copyOf` views.

## Solution 9 — Reflection vs. interface (item 65)

Three concrete costs:

1. **No compile-time type safety** — a typo in `"com.acme.ReportService"` or
   `"render"` is only discovered at runtime (`ClassNotFoundException`,
   `NoSuchMethodException`).
2. **Performance** — `getMethod`/`invoke` are far slower than a direct call
   (reflection overhead per invocation).
3. **Encapsulation bypass** — reflection ignores `private`/access checks, so
   the code is coupled to implementation details instead of a contract.

Rewrite:

```java
public interface Reporter { String render(int id); }

public final class ReportService implements Reporter {
    @Override public String render(int id) { return "<html>report " + id + "</html>"; }
}

String html = new ReportService().render(42);   // direct, typed, fast, safe
```

Each cost is removed: wrong names fail at compile → no runtime surprises; the
call is a direct interface dispatch → no reflection overhead; and everything
goes through the `Reporter` contract → no reaching into private internals.

## Solution 10 — Native or JDK? (item 66)

- **(a) file checksum** → Java already has it: `MessageDigest` (`SHA-256` etc.)
  or `java.security`. No native needed.
- **(b) environment variable** → `System.getenv()`; no native needed.
- **(c) vendor C library for a hardware sensor** → **arguably justified**: the
  JDK has no way to talk to arbitrary vendor hardware, so a thin, isolated JNI
  wrapper around the vendor's C library is the legitimate case.
- **(d) sorting** → Java already does it: `Arrays.sort` / `Collections.sort`
  (timsort). No native needed — and native sorting would forfeit portability
  for no measured gain.

Rule of thumb: if the JDK has it, use it; native is only for what Java can't
do, wrapped thin.

## Solution 11 — Optimize, measured (item 67)

Two reasons the caching is pointless/harmful:

1. **It caches the wrong thing.** `wordCount` splits the string — the dominant
   cost is the `split`, not computing `s.length()` (which is O(1)). Caching the
   cheap part and doing the expensive part every call optimizes nothing.
2. **Key collisions / unsafe cache.** A `HashMap<Integer,Integer>` keyed on hash
   (or length, which many strings share) stores bogus cross-string results, and
   a static unsynchronized map is a correctness/concurrency hazard on top of a
   non-optimization.

Correct order: **profile first** (a profiler like JFR/async-profiler, or a JMH
microbenchmark) to find the real cost; then optimize *that* — e.g. if profiling
shows `split` is the hot spot, use `indexOf(' ')`-based counting or a
`StringTokenizer`/`Scanner` alternative, keep the simple version as a baseline,
and re-measure to confirm the win stays correct.

## Solution 12 — Rename for readability (item 68)

```java
public class Product {                     // class name: PascalCase
    public int quantity;                   // field: camelCase, full word
    public boolean isAvailable;            // boolean: is/has prefix
    public void changeStock(int amount) { ... }  // method: verb camelCase
}
```

Rules applied:
- **type** `pr` → `Product` (`PascalCase`, meaningful noun).
- **field** `n` → `quantity`, `iv` → `isAvailable` (`camelCase`, full words; a
  boolean gets `is`/`has`/`can`).
- **method** `cs(...)` → `changeStock(amount)` (verb + object, `camelCase`,
  descriptive parameter).
- **package/casing**: if the class were in a package it'd be lowercase
  reverse-domain; the class is a single top-level `public` type.
