# Chapter 12 — Serialization (items 85–90)

The story of `Serializable`: avoid it, but if you must use it, do so with extreme
care. All six items are built in one round, with two runnable demos
(`SerializationBasicsDemo` for 85–87, `SerializationSafetyDemo` for 88–90).

---

## Item 85 — Prefer alternatives to Java serialization

Java's built-in serialization is an opaque binary format that carries object
graphs (class descriptors, references, the whole transitive set). It has real
costs:

- **Security** — deserializing untrusted data is the classic gadget-injection
  attack surface; any `Serializable` class on the classpath becomes a possible
  gadget.
- **Fragility** — the stream is tied to the class's *internal layout*: rename a
  field, change a type, restructure the class, and old bytes break or silently
  change meaning.
- **Complexity** — you inherit the whole mechanism (versioning, cycles,
  proxies) whether you want it or not.

Prefer safe, explicit alternatives: JSON, CSV, protobuf, or a small
hand-rolled text/byte format that is *versioned* and *decoupled* from your
internal representation.

**Sample:** `BadJavaSerialization` is a `Serializable` record round-tripped
through `ObjectOutputStream` — a 99-byte opaque blob tied to the class layout,
and a deserialization gadget risk. `GoodDataFormat` uses a versioned,
human-readable `v1|GoodDataFormat|name|age` record: explicit, forward/backward
compatible, and with no object-graph deserialization (immune to gadget
injection). `SerializationAlternativeTest` proves both round-trip and that the
good format rejects unknown versions/shapes.

---

## Item 86 — Implement `Serializable` with great caution

Making a class `Serializable` is a permanent decision with real costs:

- The class's public API becomes effectively **permanent** — changing the
  serialized form later breaks old streams.
- **`serialVersionUID`** must be managed explicitly, or the JVM computes one
  from the exact class and any change silently invalidates the stream.
- For **extendable, mutable** classes the hazards multiply: equals/hashCode
  become bound to the representation, and every subclass inherits the burden.
- Serializing internal mutable state bypasses your encapsulation.

Only implement it when you genuinely need streaming persistence, and then
deliberately: immutable, stable representation, explicit UID, and documented.

**Sample:** `BadPrecariousSerializable` is a public, mutable, extendable class
with no `serialVersionUID` and a hand-rolled `readObject` that keeps a stream
handle — the whole internal representation leaks. `GoodMinimalSerializable` is
a deliberate, immutable value type with an explicit `@Serial serialVersionUID`
and a stable representation. `SerializableCautionTest` round-trips both.

---

## Item 87 — Consider a custom serialized form

When you must serialize, the *default* form writes every non-transient field —
tying the stream to your internal representation. A **custom form** writes only
the *logical* data (as stable primitives) in a private `writeObject`, and
reconstructs it in `readObject`. The internal representation can then change
without breaking the stream, and the bytes are the minimum needed.

**Sample:** `BadDefaultSerializedForm` is a `Date`-range with no custom
`writeObject`/`readObject` — the default form serializes the full internal
`Date` objects. `GoodCustomSerializedForm` writes just two `long` epoch-millis.
`CustomFormTest` proves the good form round-trips the same logical value in
*fewer* bytes (105 vs 156 in the run) and is decoupled from the internal type.

---

## Item 88 — Write `readObject` methods defensively

Even with a constructor that validates its arguments, a class that takes a
default `readObject` can be fed a crafted stream that violates its invariants.
`readObject` must do the same job as the constructor:

- **Validate** every invariant (restore the class's contract) — throw
  `InvalidObjectException` on a violating stream.
- **Defensively copy** any mutable field the object exposes, so callers can't
  alias or mutate the internals through a getter.

**Sample:** `BadUnvalidatedReadObject` reads with the default `readObject` and
returns its internal mutable `Date`s from `start()`/`end()` — accept a crafted
out-of-order pair, or mutate a returned `Date`, and the value range is corrupt.
`GoodDefensiveReadObject.readObject` re-validates (`InvalidObjectException` on
violation) and both the field copies and the getters return copies.
`DefensiveReadObjectTest` shows the bad one correlates to `-90000ms` while the
good one stays `8000ms`.

---

## Item 89 — For instance control, prefer enum types to `readResolve`

A "singleton" implemented as a class must be defended against serialization, or
deserialization mints a *second* instance. The idiomatic fix — `readResolve` —
is fragile: it must be exactly right, agree with `equals`, and silently
discards serialized state in favour of the resolved singleton.

An **`enum`** gives instance control for free: the language guarantees one
instance per constant, and serialization cannot create another. Prefer it.

**Sample:** `GoodEnumSingleton.INSTANCE` is a single instance, immune to
deserialization. `BadReadResolveSingleton` relies on `readResolve` returning the
static `INSTANCE`; `InstanceControlTest` shows the enum stays one instance while
the readResolve version works only by discarding the deserialized state.

---

## Item 90 — Consider serialization proxies instead of serialized instances

The cleanest way to protect a class is to *not serialize it at all*. Write a
private static **serialization proxy** inner class that carries only the logical
data. The real class's `writeReplace()` returns the proxy; the proxy's
`readResolve()` rebuilds the real object *through its normal validating
constructor*. This guarantees invariants can never be violated by a crafted
stream, no matter how the real representation evolves.

**Sample:** `GoodSerializationProxy` (a `Period`) delegates to a private
`SerializationProxy` holding two `long`s; `readResolve` rebuilds via the
validating constructor (and `readObject` on the real class is blocked). A
crafted invalid stream is therefore *rejected*. `BadDirectSerialization`
serializes the mutable representation directly and validates nowhere.
`ProxyRoundTripTest` confirms the proxy round-trips a valid period and rejects
an invalid one with `InvalidObjectException`.

---

## Senior checklist

- [ ] Prefer versioned, explicit data formats (JSON/text/protobuf) over raw Java
      serialization; never deserialize untrusted data (85).
- [ ] Implement `Serializable` only deliberately; immutable + stable form +
      explicit `serialVersionUID` (86).
- [ ] Serialize only the logical data via a custom form, decoupled from internals (87).
- [ ] `readObject` validates invariants and defensively copies mutable fields (88).
- [ ] Enums for instance control; avoid hand-rolled `readResolve` singletons (89).
- [ ] Serialization proxy instead of serializing the real instance (90).
