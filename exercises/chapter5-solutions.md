# Chapter 5 — Solutions (items 26–29)

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