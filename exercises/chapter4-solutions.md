# Chapter 4 — Solutions (items 15–19)

## Solution 1 — Audit the public surface (item 15)

```java
public final class Telemetry {
    private final Map<String, Long> samples;
    private final int capacity;

    private Telemetry(Map<String, Long> samples, int capacity) {
        this.samples = samples;
        this.capacity = capacity;
    }

    public static Telemetry create(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        return new Telemetry(new HashMap<>(), capacity);
    }

    public void recordSample(String name, long value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (samples.size() >= capacity) {
            throw new IllegalStateException("capacity reached: " + capacity);
        }
        samples.put(name, value);
    }

    public Long sampleOf(String name) {
        return samples.get(name);
    }

    public String report() {
        return samples.toString();
    }
}
```

One sentence on changing the internal map type: because the map is private and
no client ever receives the reference, swapping `HashMap` for `LinkedHashMap`
(or anything else) is a purely internal change with zero impact on callers —
that is the whole point of hiding state.

---

## Solution 2 — Defensive accessor (item 16)

```java
public List<String> tags() {
    return new ArrayList<>(tags);
}
```

If a caller mutates the returned list, nothing inside the object changes
because the caller only ever touches a fresh copy. Your accessor is the fix
because it turns a shared, mutable internal reference into a one-shot snapshot —
the caller's changes are discarded on the next call.

---

## Solution 3 — Immutability audit (item 17)

```java
public final class Cart {
    private final List<String> items;

    private Cart(List<String> items) {
        this.items = new ArrayList<>(items);
    }

    public static Cart empty() {
        return new Cart(List.of());
    }

    public Cart withItem(String item) {
        List<String> copy = new ArrayList<>(items);
        copy.add(item);
        return new Cart(copy);
    }

    public List<String> items() {
        return new ArrayList<>(items);
    }
}
```

Rules applied:

1. No mutators — `addItem` became the functional `withItem`.
2. Class is `final`.
3. Field `items` is `final`.
4. Field `items` is `private`.
5. Mutable component never shared — the constructor copies the incoming list
   and `items()` returns a defensive copy.

Note the two defensive copies are not redundant: the constructor copy protects
against the *caller's* later mutation of the list they passed in; the accessor
copy protects against the *caller* mutating the object's state through the
getter.

---

## Solution 4 — Diagnose the double count (item 18)

`HashSet` does not override `addAll`, so it inherits
`AbstractCollection.addAll`, which is implemented as a loop calling the
overridable `add` for each element. The subclass overrode *both*, so each
element is counted twice: once by `addAll` (`count += c.size()`) and again by
the `add` calls it triggers inside. `3` elements → `3 + 3 = 6`.

The composition fix counts the *real* delta, one time only:

```java
public final class CountingSet<E> {
    private final Set<E> delegate = new HashSet<>();
    private int count;

    public boolean add(E e) {
        boolean changed = delegate.add(e);
        if (changed) count++;
        return changed;
    }

    public boolean addAll(Collection<? extends E> c) {
        int before = delegate.size();
        boolean changed = delegate.addAll(c);
        count += delegate.size() - before;
        return changed;
    }

    public int count() { return count; }
    public int size() { return delegate.size(); }
}
```

Because `delegate.addAll` is the JDK's own implementation (no subclass override
to trip over), the counted delta is exactly the number of genuinely new
elements — no double counting, and duplicates inside the add are also ignored.

---

## Solution 5 — The constructing-subclass bomb (item 19)

Sequence at `new SubClass()`:

1. The JVM starts constructing `SubClass` and immediately invokes
   `SuperClass`'s constructor (the super call happens first).
2. `SuperClass`'s constructor calls `this.overridableMethod()` — that call is
   dynamically dispatched to `SubClass`'s override, **not** the superclass's
   own implementation.
3. The override reads `SubClass`'s field `bonuses`, which has not been
   assigned yet (that assignment happens in `SubClass`'s constructor body,
   which runs only *after* the super constructor returns). The field is still
   its default value (`null`), so the override throws
   `NullPointerException` and construction aborts.

What the base-class author should have instead:

1. **Never call overridable methods from constructors** — call only `private`,
   `final`, or `static` methods, preferably with primitives, so the call cannot
   be redirected into a half-initialized subclass.
2. **Design explicitly**: either declare the class `final` (prohibit
   inheritance outright), or, if extension is a real, documented requirement,
   provide `protected` hook methods with documented contracts, initialize every
   piece of state before any hook can observe it, and document exactly which
   methods may be overridden and what calling them before super-construction
   would do.