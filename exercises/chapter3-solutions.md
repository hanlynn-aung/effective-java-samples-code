# Chapter 3 — Solutions (Items 10–14)

## 1. When to override equals

a) `Account` (mutable row object) — **don't override**; identity is correct.
   Mutable-value `equals` is inconsistent (rule 4) and mutating a stored
   object breaks every hash-based collection. If two "same" accounts must be
   recognised, compare on an explicit key (`accountId`), not by contents.
b) `CurrencyAmount` — **override**: it's a value class, and clients want
   `new CurrencyAmount(10, USD).equals(new CurrencyAmount(10, USD))`.
c) `OrderService` — **don't override**: one stateless service bean is as good
   as another; singletons/handlers are identity-semantics classes.

## 2. Full value-class triad

```java
public final class CurrencyAmount {
    private final BigDecimal amount;
    private final Currency currency;
    private final int hashCode;

    public CurrencyAmount(BigDecimal amount, Currency currency) {
        this.amount = Objects.requireNonNull(amount, "amount");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.hashCode = Objects.hash(amount, currency);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CurrencyAmount other)) return false;
        return amount.equals(other.amount) && currency.equals(other.currency);
    }

    @Override public int hashCode() { return hashCode; }

    @Override public String toString() {
        return "CurrencyAmount[" + amount + " " + currency + "]";
    }
}
```

The hash can be cached in the constructor because the object is **immutable**
— every field is final, so the hash can never change, and the cached value is
always consistent with equality.

## 3. Symmetry

The **symmetry rule** is broken: `token.equals("abc")` is `true` while
`"abc".equals(token)` is `false`, so `equals` behaves differently depending
on which operand is which. Don't special-case `String`; leave `equals`
limited to the owning type:

```java
@Override public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Token other)) return false;
    return raw.equals(other.raw);
}
```

If matching against raw strings is genuinely needed, expose an explicit
method (`Token.is(String)`) rather than corrupting `equals`.

## 4. Transitivity

The base `equals` uses `instanceof`, so a `Point` considers a `ColorPoint`
equal when coordinates match — `p.equals(red)` and `p.equals(blue)` are both
`true`. The subclass's `equals` requires `instanceof ColorPoint`, so `red`
and `blue` (different colors) are not equal to each other. Hence
`p == red`, `p == blue`, but `red != blue` — the *transitive* rule fails.
The structural reason: the subclass adds a field that the base's `equals`
doesn't know about, so there is no way to keep `x==y && y==z ⇒ x==z` while
both classes claim equality independently. Prefer **composition** (a
`ColoredPoint` holding a `Point` + `Color`) or a `record` over value-based
inheritance.

## 5. HashSet dedup

`equals` is overridden but **`hashCode` is not** (rule 1 violated). Two `Tag`s
with the same `id` are `equals` yet produce different `Object.hashCode()`
values. `HashSet.contains` first finds the *bucket* by hash code; the two
look-alikes land in different buckets, so the collision never occurs and each
is treated as unique (`size() == 2`).

Fix:

```java
@Override public int hashCode() { return Integer.hashCode(id); }
```

## 6. Clone vs copy

`Object.clone()` performs a **shallow bit-copy**: the new object shares every
reference field with the old one. For `BadShallowClonePerson` the shared field
is the `List<String> phones`, so `clone.phones().add(...)` appends to the list
the original still owns — a "copy" that mutates its source.

A correct copy, without `Cloneable`:

```java
public final class Person {
    private final String name;
    private final List<String> phones;
    public Person(String name, List<String> phones) {
        this.name = name;
        this.phones = new ArrayList<>(phones);          // defensive copy
    }
    public static Person copyOf(Person other) {
        return new Person(other.name, other.phones);    // deep via ctor copy
    }
}
```

Copy constructors/factories call real constructors, let the type system work,
and make the depth of the copy explicit — unlike `clone`'s silent sharing.

## 7. compareTo overflow

The subtraction `a.rank - b.rank` overflows when the operands straddle a
boundary — `Integer.MAX_VALUE - (-1)` wraps to a negative number, so
`compare(MAX, -1)` returns negative and sorting thinks `MAX < -1`. Broken for
any numeric sort/`TreeSet`/`TreeMap` touching the extremes.

```java
public static int compare(Priority a, Priority b) {
    return Integer.compare(a.rank, b.rank);
}
```

And `compareTo == 0` should coincide with `equals` (consistency rule), so that
`TreeSet`/`TreeMap`, which order by `compareTo` alone, never merge distinct
elements or keep duplicates of equal ones.