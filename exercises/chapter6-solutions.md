# Chapter 6 — Solutions (items 34–38)

## Solution 1 — Convert a magic-number table (item 34)

```java
enum Weekday {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    String abbreviation() {
        return name().substring(0, 3);
    }
}
```

`dayName(99)` becomes impossible to call wrongly because there is no `int`
parameter at all — the argument is a `Weekday`, so every value is necessarily
one of the true days; calling `dayName` with a `99` doesn't compile.

`values()` replaces the hand-maintained "all days" list: `for (Weekday d :
Weekday.values())` yields exactly the declared constants in order, automatically
staying current when days are added — no separate array to forget to update.

---

## Solution 2 — Hunt the ordinal corruption (item 35)

As the code exists, ordinals are `LOW=0, HIGH=1, MEDIUM=2` (declaration order).
So `LOW.weight()=0`, `MEDIUM.weight()=2`, `HIGH.weight()=1` — the intended
ordering LOW=1, MEDIUM=2, HIGH=3 is completely scrambled, silently.

Rewrite to store the weight in a field:

```java
enum Severity {
    LOW(1), HIGH(3), MEDIUM(2);
    private final int weight;
    Severity(int weight) { this.weight = weight; }
    int weight() { return weight; }
}
```

Now `LOW=1, MEDIUM=2, HIGH=3` are exact regardless of how the constants are
reordered at the top. The weight is *data*, independent of source position, so
no insertion or reordering can drift it.

---

## Solution 3 — Bit-field to EnumSet (item 36)

```java
enum Style { FIRST, BOLD, UNDERLINE }

String render(Set<Style> styles) {
    StringBuilder s = new StringBuilder();
    if (styles.contains(Style.FIRST))  s.append("f");
    if (styles.contains(Style.BOLD))   s.append("b");
    if (styles.contains(Style.UNDERLINE)) s.append("u");
    return s.toString();
}
// render(EnumSet.of(Style.BOLD, Style.UNDERLINE)) -> "bu"
```

Two reasons it's strictly safer:
1. **Undefined members cannot exist.** `EnumSet` only holds declared enum
   values, so `render(1<<20)` in the int world (which silently produces "")
   has no analogue — there's no way to even attempt an undefined style.
2. **Opaque masks become readable, named members.** `render(5)` is a mystery;
   `EnumSet.of(BOLD, FIRST)` states its intent, and `EnumSet` even lets you
   iterate/print the actual styles instead of hand-peeling bits.

---

## Solution 4 — Ordinal indexing audit (item 37)

`Size[] sizes = new Size[Size.values().length];` two concrete failure modes:

1. **Silent hole.** If the data never yields a particular `Size`, that bucket
   stays `null`. Nothing throws — the caller just gets a `null`/empty region it
   didn't expect, indistinguishable from "zero of this size".
2. **Full remap on insertion.** Add a new `Size` constant at the front (or
   middle) of the enum. `ordinal()` of every later value shifts by one, so the
   bucket that previously held "LARGE" now holds what the code believes is
   "XLARGE" — every bucket is quietly re-assigned with no compiler help.

`EnumMap` replacement guaranteeing one entry per enum value:

```java
Map<Size, List<Item>> bySize = new EnumMap<>(Size.class);
for (Size s : Size.values()) {
    bySize.put(s, new ArrayList<>());          // pre-seed every key
}
for (Item i : items) {
    bySize.get(i.size()).add(i);               // typed key, no ordinal math
}
```

Pre-seeding every `Size` in `values()` guarantees a non-null, empty list for
each enum value — no holes, no shifts, and adding a future `Size` (re)seeds it
automatically.

---

## Solution 5 — Emulate extension (item 38)

```java
interface Ranged {
    double range(double v);
}

enum BasicRange implements Ranged {
    CLAMP { public double range(double v) { return v < 0 ? 0 : v > 1 ? 1 : v; } },
    WRAP  { public double range(double v) { return ((v % 1) + 1) % 1; } };
}

enum ExtendedRange implements Ranged {
    FLIP { public double range(double v) { return 1 - v; } };
}

double applyRange(Ranged r, double v) { return r.range(v); }
```

A single `applyRange` accepts `BasicRange` *and* `ExtendedRange` interchangeably
because both are `Ranged` — callers are decoupled from which family a constant
came from, and a new family can be added without editing existing code.

**Where the idiom fails:** an interface cannot carry *all* of enum's powers. You
inherit shared `Enum` methods (`name()`, `ordinal()`, `values()`) only per
family, not across the interface; you can't iterate "all `Ranged` values" since
they live in separate enums; and enum-specific facilities like `EnumSet`/
`EnumMap` need the concrete enum type, not the interface.

## Solution 6 — Naming-pattern relapse (item 39)

`testxISay` loses its prefix, `tetsTheRealOne` misspells it — neither name
matches the filter, and since a naming pattern has zero compiler participation,
nothing reports that a method "should have run". The suite silently reports
green with tests missing; a falsely-passing (previously-passing) build hides a
regression.

Annotation + runner:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test2 { }

for (Method m : cls.getDeclaredMethods()) {
    if (m.isAnnotationPresent(Test2.class)) {
        try { m.invoke(obj); pass.add(m.getName()); }
        catch (InvocationTargetException e) { fail.add(m.getName() + " " + e.getCause()); }
    }
}
```

A typo in the annotation name (`@Teest`) is a *compile error*; a missing
annotation is a deliberate, reviewable change; and a failing test's exception
is captured and reported rather than skipped.

## Solution 7 — The missing @Override (item 40)

`Leaf` declares `getValue()`, but `Node` exposes `value()` — different names, so
`getValue()` **does not override** anything. It's a brand-new method. When a
`Leaf` is referenced through a `Node`, `node.value()` resolves to `Node`'s own
implementation (`"node"`), because there is no virtual `value()` on `Leaf` to
dispatch to. `@Override` would have made this a compile error: the compiler
checks "does this method actually override anything?" against `value()`, finds
that `getValue()` doesn't, and stops the build immediately.

```java
class Leaf extends Node {
    @Override
    public String value() { return "leaf"; }
}
```

Now `leaf.value()` and `((Node) leaf).value()` both return `"leaf"`.

## Solution 8 — Marker interface vs annotation (item 41)

Recommend `findByX(GoodQueryable query)` backed by a marker interface. Reason:
a marker interface is a **type**, so the compiler rejects a non-queryable
argument before anything runs — no reflection check to forget, no per-call
validation cost. `instanceof` additionally lets the implementation branch on
query classes (`query instanceof GoodIndexed`) that the interface alone couldn't
express. Compile-time enforcement (type) + runtime branching (`instanceof`) are
both available; an annotation marker gives you neither at compile time and
forces `isAnnotationPresent` checks at every call.

Choose an **annotation** marker instead when the property carries *data* — e.g.
`@Cacheable(ttl = 60)` needs a value, which an interface cannot supply. Then
it's reasonable to reflect over it (a single framework entry point), and you
accept the loss of compile-time type safety in exchange for attaching
parameters.