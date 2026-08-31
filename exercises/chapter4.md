# Chapter 4 — Exercises (items 15–19)

Work through these before reading the solutions. Each one maps to a real
senior-level review question.

## Exercise 1 — Audit the public surface (item 15)

A class `Telemetry` currently has:

```java
public class Telemetry {
    public Map<String, Long> samples = new HashMap<>();
    public List<String> log = new ArrayList<>();
    public void recordSample(String name, long value) { ... }
    public String report() { ... }
}
```

Rewrite it so no client can corrupt state, validations happen at the single
entry point, and construction is controlled (`create()` + private constructor).
Explain in one sentence what breaks if you later change the internal map type.

## Exercise 2 — Defensive accessor (item 16)

Write an accessor `List<String> tags()` for a private `List<String> tags` field
that guarantees callers can never modify the object's internal list — without
requiring the caller to trust you. What happens if a caller does mutate the
returned list, and why is your accessor the fix?

## Exercise 3 — Immutability audit (item 17)

Rewrite this mutable value object as immutable, then list which of the five
immutability rules you applied:

```java
public class Cart {
    private List<String> items = new ArrayList<>();
    public void addItem(String item) { items.add(item); }
    public List<String> getItems() { return items; }
}
```

Your immutable version must still let callers "add" an item — via a `with`-style
method that returns a new `Cart`.

## Exercise 4 — Diagnose the double count (item 18)

Given:

```java
class CountingSet<E> extends HashSet<E> {
    private int count;
    @Override public boolean add(E e) { count++; return super.add(e); }
    @Override public boolean addAll(Collection<? extends E> c) { count += c.size(); return super.addAll(c); }
    int count() { return count; }
}
```

`new CountingSet<>().addAll(List.of("a","b","c"))` reports `count() == 6`.
Explain exactly why (which framework method call chain produces it), then
rewrite `CountingSet` using composition so `count()` is correct for both single
adds and `addAll`.

## Exercise 5 — The constructing-subclass bomb (item 19)

A base class constructor calls a method that a subclass overrides, and the
override touches a field the subclass initializes *after* the super constructor
runs. In prose, write out the sequence of what happens at `new SubClass()` that
causes the failure. Then give the two things the base-class author should have
done instead (per item 19).