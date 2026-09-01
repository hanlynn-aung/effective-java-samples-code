# Chapter 11 — Solutions (items 78–84)

## Exercise 1 — Shared mutable data (item 78)

1. The two problems are **visibility** (another thread may never see the write
   to a non-`volatile` field; the Java memory model requires synchronization to
   establish a happens-before edge) and **atomicity** real a read-modify-write
   (`value = value + 1` is three steps — read, increment, write — and two
   threads can interleave and lose an update).

2. `volatile int` fixes **visibility** but **not atomicity**: it guarantees a
   write is seen by other threads, but `++` is still a non-atomic read-modify-
   write, so two threads can both read the same latest value and still lose an
   update. You need mutual exclusion (`synchronized`) or an atomic type.

3. Correct version:

   ```java
   // Option A: synchronized
   public synchronized int increment() { return ++value; }
   public synchronized int value()     { return value; }

   // Option B: atomic (non-blocking, often preferred)
   private final AtomicInteger value = new AtomicInteger();
   public int increment() { return value.incrementAndGet(); }
   public int value()     { return value.get(); }
   ```

## Exercise 2 — Excessive synchronization (item 79)

Calling alien code (`l.onEvent()`) while holding the monitor is dangerous
because you don't control what a listener does: it may **block**, **re-enter
your lock** (deadlock), or throw, stalling every unrelated thread that needs
the same lock. The fix — snapshot the listeners, then invoke outside the
synchronized region:

```java
private final List<Listener> listeners = new CopyOnWriteArrayList<>();

public void notifyListeners() {
    List<Listener> snapshot = new ArrayList<>(listeners);  // no lock held here
    for (Listener l : snapshot) { l.onEvent(); }           // alien, outside lock
}
```

`CopyOnWriteArrayList` gives a thread-safe read snapshot with no lock needed to
iterate.

## Exercise 3 — Executors (item 80)

```java
static int runWell(int n) throws Exception {
    ExecutorService exec = Executors.newFixedThreadPool(4);
    List<Future<Integer>> futures = new ArrayList<>();
    try {
        for (int i = 0; i < n; i++) { futures.add(exec.submit(() -> compute(i))); }
    } finally {
        exec.shutdown();                                   // no new tasks
    }
    exec.awaitTermination(10, TimeUnit.SECONDS);           // bounded wait
    int sum = 0;
    for (Future<Integer> f : futures) { sum += f.get(); }  // collect results
    return sum;
}
```

Benefits over hand-rolled threads: a concurrency cap (4), thread re-use, easy
result collection via `Future`, a bounded clean `shutdown`/`awaitTermination`,
and interruptible tasks via `shutdownNow()`.

## Exercise 4 — Concurrency utilities (item 81)

```java
private final BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(16);

void put(int v) throws InterruptedException { buffer.put(v); }
int  take()    throws InterruptedException { return buffer.take(); }
```

Two bugs the hand-rolled `wait`/`notify` could silently have that the queue
removes: (a) **lost or missed wakeup** — calling `notify`/`notifyAll` when no
thread is waiting, or `wait` on a condition that can already be satisfied,
causing a hang; (b) a **spurious/unrelated wakeup** waking a waiter whose
condition is still false, where forgetting to re-check in a `while` loop lets
it proceed incorrectly. `BlockingQueue` handles all of this correctly by
construction.

## Exercise 5 — Document thread safety (item 82)

```java
/**
 * A thread-safe, expiry-free string cache.
 *
 * <p><b>Thread safety:</b> this class is <em>unconditionally thread-safe</em>.
 * All individual operations ({@code put}, {@code get}, {@code contains}) are
 * safe to call concurrently from multiple threads without external
 * synchronization.
 *
 * <p>Compound read-modify-write sequences (e.g. check-then-act, get-then-put)
 * are <em>not</em> atomic and must be performed by callers with their own
 * locking, or via the dedicated atomic methods.
 */
```

This states the level (unconditionally thread-safe), that individual ops are
safe, and flags that compound sequences still need caller coordination.

## Exercise 6 — Lazy initialization (item 83)

```java
// Instance field - volatile + double-checked locking
public class Lazy {
    private volatile Expensive heavy;

    public Expensive getHeavy() {
        Expensive result = heavy;
        if (result == null) {
            synchronized (this) {
                result = heavy;
                if (result == null) { result = new Expensive(); heavy = result; }
            }
        }
        return result;
    }
}

// Static field - holder-class idiom
public class Registry {
    private static final class Holder {
        static final Registry INSTANCE = new Registry();
    }
    public static Registry getInstance() { return Holder.INSTANCE; }
}
```

`volatile` is required in the DCL instance-field version so a thread that reads
the field without the lock actually sees the fully-constructed object
(publishes safely). In the holder version no `volatile`/lock is needed because
the JVM serializes class initialization: `Holder.INSTANCE` is only assigned once,
during synchronized class loading, so every reader sees a correctly-published
value.

## Exercise 7 — Thread scheduler (item 84)

Two problems with the busy-wait: (a) it **wastes CPU** spinning on `yield()`
and (b) its behaviour is **timing/scheduler-dependent** — if the worker thread
never gets to run, or runs slowly, the spinner keeps "helping" the scheduler in
a fragile, hard-to-reason-about way that fails under load or on other platforms.

```java
private final CountDownLatch done = new CountDownLatch(1);

void finishWork() { try { ... } finally { done.countDown(); } }

boolean waitUntilDone(long timeoutMs) throws InterruptedException {
    return done.await(timeoutMs, TimeUnit.MILLISECONDS);   // fails cleanly
}
```

A `CountDownLatch` blocks efficiently on a *state event* (not timing), and the
timeout guarantees the caller fails cleanly instead of spinning forever or
hanging on a thread the scheduler never schedules.
