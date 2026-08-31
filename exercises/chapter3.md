# Chapter 3 — Exercises (Items 10–14)

## 1. Concept — when to override equals

A value class and an "active" class both live in your module. For each, state
whether `equals` should be overridden and what equality means:

a) `Account` — a mutable row object loaded from the DB, used by services.
b) `CurrencyAmount` — immutable, with `amount` and `currency` fields.
c) `OrderService` — a stateless service bean fetched from the container.

## 2. Write — full value-class triad

For an immutable `CurrencyAmount` (`BigDecimal amount`, `Currency currency`),
write `equals`, `hashCode`, and `toString` that satisfy the Object contract.
Use `instanceof` pattern matching and `Objects.hash`/`hash` on the fields.
Then explain why the hash can be cached in the constructor.

## 3. Debug — the symmetry bug

```java
public final class Token {
    private final String raw;
    public Token(String raw) { this.raw = raw; }
    @Override public boolean equals(Object o) {
        if (o instanceof Token t) return raw.equals(t.raw);
        if (o instanceof String s) return raw.equals(s);
        return false;
    }
}
```

`new Token("abc").equals("abc")` is `true` but `"abc".equals(new
Token("abc"))` is `false`. Which rule is broken, and what is the fix?

## 4. Explain — why transitivity fails

The `BadTransitivityColorPoint` demo shows `p.equals(red)`, `p.equals(blue)`,
yet `red != blue`. Draw the violation and explain *why* adding a field to a
`equals`-overriding base class can never keep transitivity.

## 5. Fix — HashSet dedup

```java
public final class Tag {
    public final int id;
    public Tag(int id) { this.id = id; }
    @Override public boolean equals(Object o) {
        return o instanceof Tag t && id == t.id;
    }
}
```

`new HashSet<>(List.of(new Tag(1), new Tag(1)))` has `size() == 2`. Explain
exactly which contract rule is missing and what `contains` does without it.

## 6. Reason — clone vs copy

`BadShallowClonePerson.clone()` returns a full clone, yet mutating the
clone's phone list edits the original. Why does `super.clone()` behave that
way, and what would a correct copy implementation look like (no `Cloneable`)?

## 7. Fix — compareTo without overflow

```java
public final class Priority {
    public final int rank;
    public Priority(int rank) { this.rank = rank; }
    public static int compare(Priority a, Priority b) { return a.rank - b.rank; }
}
```

Find the two callers this breaks (values and collections), fix the overflow,
and state the rule `compareTo == 0` must respect to keep `TreeSet`/`TreeMap`
well-behaved.