# Chapter 12 — Exercises (items 85–90)

## Exercise 1 — Alternatives to Java serialization (item 85)

Give two concrete problems with using Java's built-in serialization as your
interchange format, and describe what a versioned text format (e.g.
`v1|Person|Alice|30`) gives you that a raw `ObjectOutputStream` blob does not.

## Exercise 2 — Serializable caution (item 86)

A library class `MoneyBigDecimal` implements `Serializable` and declares no
`serialVersionUID`. Name two costs of this choice and what you would change to
make it safe.

## Exercise 3 — Custom serialized form (item 87)

```java
public final class Temperature implements Serializable {
    private final double celsius;         // logical value
    private final Unit unit;              // CELSIUS / FAHRENHEIT (enum)
    // ... constructor validates unit, converts celsius to the same scale
}
```

Write a private `writeObject`/`readObject` pair that serializes only the
logical form (a `double` and a `String`/`ordinal`) as primitives, decoupled
from the internal representation, re-validating on read.

## Exercise 4 — Defensive readObject (item 88)

A `Period` stores two mutable `Date`s. Explain why a plain
`in.defaultReadObject()` is insufficient, and rewrite `readObject` so it (a)
restores the `start <= end` invariant and (b) protects the mutable internals
from external mutation via the getters.

## Exercise 5 — Enum vs readResolve (item 89)

A hand-rolled singleton relies on `readResolve()` returning a static `INSTANCE`.
State two ways this is fragile, and show the enum form that gives instance
control for free.

## Exercise 6 — Serialization proxy (item 90)

For a `Period` with final `Date` fields, explain how a private nested
`SerializationProxy` plus `writeReplace`/`readResolve` guarantees the class
invariant cannot be violated by a crafted stream, and why this is cleaner than
a hand-rolled defensive `readObject`.
