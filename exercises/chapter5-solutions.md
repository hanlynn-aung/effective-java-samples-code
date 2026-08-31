# Chapter 5 — Solutions (items 26–33)

## Solution 1 — Raw types at the browser (item 26)

`("cat") = items.get(0)` — the raw list stored a `String`, the cast succeeds,
prints "cat". `(String) items.get(1)` — the raw list stored an `Integer`, the
cast to `String` throws `ClassCastException`.

The compiler could not catch the second bug because a **raw** `List` has no
element-type information at compile time: `items.add(42)` compiles (anything
fits a raw collection), `(String) items.get(1)` compiles (the claim is
checked only by the runtime cast). Erasure means nothing exists at compile time
to verify the stored value's type against the read. The price of raw types is
moving every such guarantee to a runtime `ClassCastException`.

---

## Solution 2 — Auditing a suppression (item 27)

The suppression is **not** justified. The cast says "this heap object really is
a `List<Integer>`", but the same heap object was filled with `String`s; no
amount of casting changes what is inside it. The first read of the "ints" list
throws `ClassCastException`. Indiscriminate casting + suppression is
unsound by construction.

Safe rewrite:

```java
List<String> strings = List.of("1", "2");
List<Integer> ints = strings.stream()
        .map(Integer::parseInt)
        .toList();
```

Reason: `parseInt` *transforms* each `String` into an honest `Integer` in a new
list — the operation is provably safe because the values genuinely become
`Integer`s. The cast version was never safe because it only relabels the same
heap object whose contents were born `String`.

---

## Solution 3 — The covariant trap (item 28)

`Object[] o = new Long[3]` is legal because arrays are **covariant** — a
`Long[]` is a subtype of `Object[]`. Arrays are also **reified**: at runtime the
array remembers its real component type (`Long`), so the later `o[0] = "x"` is
checked at runtime and throws `ArrayStoreException`.

The generic equivalent does not compile because generics are **invariant** —
`ArrayList<Long>` is not a subtype of `List<Object>` — and because their type
arguments are **erased** at runtime (nothing would remember the `Long` to check
the store). Invariance + erasure means the compiler refuses the assignment
instead of deferring the bug to the first store.

---

## Solution 4 — Design a generic class (item 29)

```java
public final class GoodQueue<E> {
    private final List<E> backing = new LinkedList<>();

    public void enqueue(E element) { backing.add(element); }

    public E dequeue() {
        if (backing.isEmpty()) {
            throw new IllegalStateException("queue is empty");
        }
        return backing.remove(0);
    }

    public boolean isEmpty() { return backing.isEmpty(); }
}
```

By choosing a `List<E>` backing, no element cast is ever needed: `add` and
`remove(0)` are already fully typed.

An alternative storage that would need an unchecked cast is an `Object[]`
buffer (`elements[tail++]` when circular), read back as `(E)`. The safe place
for the suppression is precisely the single `dequeue` read (`@SuppressWarnings`
on the local read `E e = (E) elements[tail]`), accompanied by a comment proving
that only `enqueue` ever writes into the array and always with an `E`.

---

## Solution 5 — Annotate the helper (item 30)

It fails because `values` is a raw `Set`, so its iterator yields `Object`, and
`compareTo` does not exist on `Object` — and the method returns a raw
`Comparable`, forcing callers to cast. The minimal generic version brings the
element type into the signature:

```java
static <E extends Comparable<? super E>> E max(Set<E> values)
```

- `<E extends Comparable<? super E>>` is the recursive bound: "E knows how to
  compare to itself". The `? super E` matters because (for example) `Integer`
  implements `Comparable<Integer>`, but a `Set<Number>` should still be maxable
  even though `Number` itself doesn't implement `Comparable<Number>`.
- Returning `E` means no caller-cast for `String`s, `Integer`s, or anything
  else satisfying the bound.

---

## Solution 6 — PECS by hand (item 31)

1. `void fill(List<? extends E> source, List<? super E> target)` — `source`
   **producer** (we read from it): `extends`. `target` **consumer** (we write
   into it): `super`.
2. `boolean addAll(Collection<? extends E> values)` — the receiving collection
   is the consumer (it absorbs `values`); `values` produces, so `extends`.
3. `T pick(Collection<? extends E> options)` — `options` produces the returned
   element, so `extends`.

General check: put the *other* collection in the method signature and reason
about who yields and who receives. A parameter that both reads and writes must
stay plainly `E`.

---

## Solution 7 — Varargs audit (item 32)

At the call site the compiler materializes the arguments into an actual array
of type `List[]` (generic arrays can't exist, so `List<String>[]` is really a
plain `List[]` — the component type is erased). Inside `dangerous`, `stringLists`
is that real array; aliasing it into `Object[]` and writing `List.of(42)` puts
a non-`String` value into a slot the caller believes is `List<String>`. That
single write is *heap pollution*.

The caller's compiler warning is precisely: "varargs parameter from generic
array creation may cause heap pollution" — the JDK telling you the array's
element type is not reified, so a wrong write here can never be caught at the
write.

`@SafeVarargs` cannot be applied because the method **is not safe**: it stores
into the array (it pollutes). The annotation is a *claim of safety* — you may
only attach it when you never modify, leak, or unsafely cast the varargs array.

---

## Solution 8 — The heterogeneous container (item 33)

Trace:

1. The raw cast gets `String.class` through as an unchecked `Class<String>`
   (the `(Object)` hop makes the cast compile; the runtime object is still
   `String.class`).
2. `put` runs `type.cast(instance)` — i.e. `String.class.cast(Integer.valueOf(7))`.
3. `Class.cast` checks "is this value an instance of String?" — no — and throws
   `ClassCastException` **inside `put`**, so the inconsistent
   `(String.class → Integer)` pair never enters the map.

That is the whole point of the pattern's runtime check at the boundary: the
`Class<T>` key already knows the contract, and `type.cast` enforces it the
moment a value tries to attach itself to a key it doesn't satisfy. Without it
(the `Bad` string-keyed version), the inconsistency sails in and only blows up
at some unrelated read far away.