# Chapter 12 — Solutions (items 85–90)

## Exercise 1 — Alternatives to Java serialization (item 85)

Two problems: (1) **security** — deserializing an untrusted `ObjectOutputStream`
blob is the classic gadget-injection attack surface; (2) **fragility** — the
stream is bound to the class's internal field layout, so renaming/retargeting a
field or restructuring the class breaks or reinterprets old bytes.

A versioned text format (`v1|Person|Alice|30`) is explicit and decoupled: it
documents its own shape, a reader for `v1` keeps reading old records as the
model evolves, and there is no magic object-graph deserialization (immune to
gadget injection).

## Exercise 2 — Serializable caution (item 86)

Costs: (1) the public serialized form becomes effectively **permanent** — the
internal representation is exposed and evolving it breaks the stream; (2) with
no explicit `serialVersionUID`, the JVM computes one from the exact class, so
almost any change invalidates previous streams silently. Make it safe by
declaring an explicit `@Serial long serialVersionUID`, keeping the class
immutable and the representation stable, and having it coupled to logical data.

## Exercise 3 — Custom serialized form (item 87)

```java
private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeDouble(celsius);
    out.writeObject(unit.name());       // stable String, not bytecode-dependent
}

private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    double c = in.readDouble();
    Unit u = Unit.valueOf((String) in.readObject());
    if (u == null) {
        throw new InvalidObjectException("unknown unit");
    }
    // assign to final fields is not possible in readObject on all types; use
    // non-final fields or a serialization proxy (item 90) for the clean form.
    celsius = c;
    unit = u;
}
```

The stream carries only the logical `double` + unit name as primitives, so the
internal representation can change freely, and read re-validates.

## Exercise 4 — Defensive readObject (item 88)

A plain `defaultReadObject()` is insufficient because a crafted stream can write
the two `Date`s in any order (breaking `start <= end`) and because the internal
mutable `Date`s become aliases that callers can mutate through the getters.

```java
private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    Date s = new Date(start.getTime());
    Date e = new Date(end.getTime());
    if (s.after(e)) {
        throw new InvalidObjectException("invalid period: " + s + " after " + e);
    }
    start = s;   // store copies, never the attacker's aliases
    end = e;
}

public Date start() { return new Date(start.getTime()); }  // getters return copies
public Date end()   { return new Date(end.getTime()); }
```

## Exercise 5 — Enum vs readResolve (item 89)

Fragility of a `readResolve` singleton: (1) it must be exactly right about what
the resolved instance is, and (2) any serialized *state* is silently discarded
in favour of the resolved singleton, so a deserialized "instance" is not what
was written. An enum eliminates both:

```java
public enum Registry {
    INSTANCE;   // language guarantees one instance; serialization can't make another
}
```

## Exercise 6 — Serialization proxy (item 90)

The real class never serializes its internals — it returns a private
`SerializationProxy` holding only the logical `long`s via `writeReplace()`. On
read, the proxy's `readResolve()` calls the real class's **validating
constructor**, so every invariant is re-established through the same code path
a normal caller uses; a crafted stream can't reach the internals at all. This is
cleaner than a defensive `readObject` because it works naturally with `final`
fields and decouples the stored form entirely from the real representation.
