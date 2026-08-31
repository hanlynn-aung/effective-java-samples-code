# Chapter 5 — Exercises (items 26–29)

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