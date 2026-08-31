# Chapter 5 — Exercises (items 26–33)

Work through before reading the solutions.

## Exercise 1 — Raw types at the browser (item 26)

```java
List items = new ArrayList();
items.add("cat");
items.add(42);
```

What happens at runtime when a caller runs `String first = (String) items.get(0);`
and when they run `(String) items.get(1)`? Explain why the compiler could not
catch the second bug at write time.

## Exercise 2 — Auditing a suppression (item 27)

Review this cast and decide whether the suppression is justified:

```java
List<String> strings = new ArrayList<>();
// ...
@SuppressWarnings("unchecked")
List<Integer> ints = (List<Integer>) (List<?>) strings;
```

Rewrite the code so the same operation is *provably* safe (hint: build the
`List<Integer>` from transformed values instead of casting the same heap
object), and say why the cast version was never safe.

## Exercise 3 — The covariant trap, in your own words (item 28)

Write 2–3 sentences explaining why `Object[] o = new Long[3]; o[0] = "x";`
compiles but throws `ArrayStoreException`, and why the equivalent generic code
`List<Object> l = new ArrayList<Long>();` does **not** compile at all. Name the
two properties (covariance, reification) and which one each mechanism uses.

## Exercise 4 — Design a generic class (item 29)

Design a `Queue<E>` that stores elements in a `List<E>` backing field. It must:
- expose `void enqueue(E)`, `E dequeue()`, `boolean isEmpty()`;
- throw fast on an empty dequeue;
- contain **no unchecked casts** (you may choose the backing storage freely
  to avoid them).

Explain one alternative storage choice that *would* need an unchecked cast,
and where exactly you would place the suppression.

## Exercise 5 — Annotate the helper (item 30)

Why does this fail to compile, and what is the minimal generic signature that fixes it?

```java
static Comparable max(Set values) { ... raw impl ... }
```

Then write the *recursive bound* signature for a `max` that works for `String`s,
`Integer`s, and any `Comparable`-of-itself type.

## Exercise 6 — PECS by hand (item 31)

Mark each of these parameter types as `? extends`, `? super`, or plain `T`,
with a one-line reason:

1. `void fill(List<___> source, List<___> target)` — copies every element of
   `source` into `target` (`source` yields, `target` receives).
2. `boolean addAll(Collection<___> values)` — adds `values` into this
   (consumer) collection.
3. `T pick(Collection<___> options)` — returns one element.

## Exercise 7 — Varargs audit (item 32)

Explain in 2–4 sentences how `stringLists` becomes poisonable in this method,
what the caller's compiler warning is telling you, and why `@SafeVarargs`
cannot be applied to it:

```java
static void dangerous(List<String>... stringLists) {
    Object[] array = stringLists;
    array[0] = List.of(42);
}
```

## Exercise 8 — The heterogeneous container (item 33)

With `GoodFavorites`, trace runtime behavior of this snippet and explain why
`put` itself throws instead of letting an inconsistent value into the map:

```java
GoodFavorites f = new GoodFavorites();
f.put((Class)(Object) String.class, Integer.valueOf(7));
```