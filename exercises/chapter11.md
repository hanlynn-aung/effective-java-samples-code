# Chapter 11 — Exercises (items 78–84)

## Exercise 1 — Shared mutable data (item 78)

A counter is declared with a plain `int` and incremented from several threads
via `value = value + 1`, with no synchronization and no `volatile`.

1. Name the two distinct correctness problems this introduces.
2. Explain *why* `volatile int` would fix only one of them.
3. Rewrite so the counter is exactly correct under high concurrency.

## Exercise 2 — Excessive synchronization (item 79)

```java
public synchronized void notifyListeners() {
    for (Listener l : listeners) { l.onEvent(); }   // alien code under lock
}
```

Explain why invoking alien code while holding `lock` is dangerous, and rewrite
so the listeners are invoked safely without holding the lock across the calls.

## Exercise 3 — Executors (item 80)

Rewrite the following with an `ExecutorService` so it: bounds concurrency to 4,
runs the task, and returns the computed value; include a bounded, clean shutdown:

```java
// computes some Integer
static Integer compute(int input) { ... }

static int runBadly(int n) throws InterruptedException {
    List<Thread> ts = new ArrayList<>();
    for (int i = 0; i < n; i++) { Thread t = new Thread(() -> compute(i)); t.start(); ts.add(t); }
    for (Thread t : ts) { t.join(); return t.getState() == null ? 0 : 1; }  // contrived
    return -1;
}
```

## Exercise 4 — Concurrency utilities (item 81)

A producer fills a buffer and a consumer drains it. Rewrite using
`ArrayBlockingQueue` instead of hand-rolled `wait`/`notify`, and state two bugs
the hand-rolled version could silently have that the queue eliminates.

## Exercise 5 — Document thread safety (item 82)

Write the class-level Javadoc thread-safety statement for a `HashMap`-backed
cache that you intend to be used concurrently. Include: which level it is, what
individual operations are safe, and what compound sequences (if any) the
caller must still synchronize.

## Exercise 6 — Lazy initialization (item 83)

Given a lazily-initialized, concurrently-shared field, rewrite it correctly
using double-checked locking (instance field) and the holder-class idiom (a
static field). Explain why the `volatile` is required in one and not the other.

## Exercise 7 — Thread scheduler (item 84)

```java
boolean done = false;
void waitUntilDone() {
    while (!done) { Thread.yield(); }   // busy-wait
}
```

Explain two problems with this approach, then rewrite using a `CountDownLatch`
with a timeout so it can fail cleanly instead of spinning or hanging forever.
