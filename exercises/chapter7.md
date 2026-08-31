# Chapter 7 — Exercises (items 42–44)

## Exercise 1 — De-anonymize (item 42)

```java
Executor exec = new Executor() {
    @Override public void execute() { System.out.println(task); }
};
```

Rewrite using a lambda (or a named functional interface if you prefer). Then
state precisely what `this` refers to inside the anonymous class versus inside
the lambda, and give one scenario where that difference matters.

## Exercise 2 — Concurrency of references (item 43)

For each of these, write the equivalent lambda, then say which method-reference
shape it is (static / bound / unbound / constructor):

1. `String::toUpperCase`
2. `LocalDate.now()::isAfter`
3. `Math::max`
4. `Integer[]::new`
5. `this::toString`

## Exercise 3 — Map the callback to a standard interface (item 44)

```java
interface RetryTimer { void schedule(int delayMs, TimerTask task); }
```

Which standard functional interface (if any) matches `delayMs -> TimerTask`?
If it's a two-argument case, name the `Bi*` equivalent. State when a custom
interface would genuinely be justified despite the standard one existing.

## Exercise 4 — The four standard slots (item 44)

Name the single-letter standard interfaces that fit these descriptions, and
give a one-line real-world use for each:

- produces a value with no input
- consumes one value with no output
- maps one value to another
- tests one value, returns a boolean

Then write a `Predicate<String> nonBlank` and a `Function<Integer,String>`
using method references (not lambdas) — touching items 43 + 44 together.

## Exercise 5 — Stream or loop? (item 45)

Given a `List<Transaction>` with `getAmount()` and `getDate()`, write the daily
totals. Show (a) a clean stream pipeline and (b) a plain loop, and say, with
one concrete reason, which you'd ship at a code review — and why.

## Exercise 6 — Purify it (item 46)

```java
Map<String, Integer> totals = new HashMap<>();
transactions.stream().forEach(t -> totals.merge(t.getName(), t.getAmount(), Integer::sum));
```

Rewrite using `Collectors.toMap` (or `groupingBy`) so there is no side effect,
and explain why the original is both order-dependent and unsafe to parallelize.

## Exercise 7 — Return type judgment (item 47)

An API method `frequentWords()` can return either `Stream<String>` or
`Collection<String>`. A caller wants to print the count, then print the words,
then pass the list to another method. Which return type serves the caller
better, and what happens if it's a `Stream`?

## Exercise 8 — Parallel audit (item 48)

`employees.parallelStream().forEach(e -> cache.put(e.id, e))` into a shared
`HashMap` — the developer "made it fast" with `parallelStream`. Explain exactly
why this is unsafe (two distinct reasons), and give the safe rewrite.
