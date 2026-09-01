# Chapter 11 — Concurrency (items 78–84)

Writing concurrent programs that are correct, safe, and maintainable. The
chapter is built in one round covering all seven items, with two runnable demos
(`ConcurrencyBasicsDemo` for 78–80, `ConcurrencyUtilitiesDemo` for 81–84).

---

## Item 78 — Synchronize access to shared mutable data

The Java memory model does not guarantee that a thread *sees* another thread's
writes to ordinary (non-`volatile`, non-final, non-atomic) fields without
synchronization. Worse, an unsynchronized read-modify-write (`count = count + 1`)
is **not atomic** on its own: two threads can both read the same stale value,
both increment, and both write — losing an update.

Two distinct problems:

- **Visibility** — is a write guaranteed to be seen by another thread?
- **Atomicity** — is the operation indivisible in the face of concurrent runs?

`synchronized` solves both (it establishes happens-before and mutual exclusion).
`volatile` solves visibility but **not** atomicity — use it only for flags whose
reads/writes are otherwise atomic. The `java.util.concurrent.atomic` classes
(`AtomicLong`, etc.) solve both without blocking for simple counters.

**Sample:** `BadSharedCounter` is a plain `int` with unsynchronized
`increment()`. `GoodMonitoredCounter` uses `synchronized` methods; `GoodSynchronizedCounter`
uses an `AtomicInteger`. `SharedDataTest` hammers all three with 8 threads ×
100k increments: the bad counter loses updates and never reaches the expected
total, while both good counters land exactly on 800,000.

---

## Item 79 — Avoid excessive synchronization

Synchronization is safety, but too much of it — especially calling **alien code**
(anything you don't control, e.g. a listener callback) **while holding a lock** —
is a bug factory:

- You don't know what the callback does; it may block, re-enter your lock, or
  throw, stalling or deadlocking your whole object for unrelated callers.
- The lock should be held only around the *shared-state* mutation, never around
  arbitrary downstream work.

The fix: keep the lock scope minimal, and call alien code *outside* the
synchronized region (typically after copying/snapshotting the data you need).

**Sample:** `BadHoldsLockInCallback.addListener`/`setReady` call each registered
`Listener.onReady()` **while holding `lock`**. If a listener is slow (or calls
back into `addListener`), every other thread needing the lock — even one doing
an unrelated add — is blocked. `ExcessiveSyncTest` proves exactly this: while a
slow listener runs under the bad lock, a concurrent `addListener()` cannot
proceed, whereas the snapshot-then-notify design of `GoodMinimalSync` lets it
proceed immediately.

---

## Item 80 — Prefer executors, tasks, and streams to threads

Hand-rolling `new Thread(...).start()` gives you none of the lifecycle you need
and a lot of the pain: no easy way to wait for many tasks, no concurrency cap,
no re-use, no clean shutdown, no collected results. An `ExecutorService` gives
you all of that for free:

- `Executors.newFixedThreadPool(n)` bounds concurrency and re-uses threads.
- `submit(Callable)` returns a `Future<T>` so you can *collect results*.
- `shutdown()` + `awaitTermination(...)` give a bounded, clean shutdown; tasks
  that don't finish in time can be interrupted via `shutdownNow()`.

Since Java 8, much of this reduces further to `CompletableFuture` and parallel
**streams** — prefer those where a data-parallel task maps to a stream.

**Sample:** `BadRawThread` spawns one thread per task into a pile and `join()`s
them, with no cap and no results. `GoodExecutor` uses a fixed pool, submits
`Callable`s and sums the returned `Future`s. `ExecutorTest` confirms the bad
version only "runs" tasks while the good version returns a computed sum
(`0²+1²+2²+3²+4² = 30`).

---

## Item 81 — Prefer concurrency utilities to `wait` and `notify`

`wait`/`notify` are the primitives, but they are easy to get wrong: you must
`wait` in a `while` loop re-checking the condition, use `notifyAll` not
`notify`, and guard the whole thing. The higher-level utilities in
`java.util.concurrent` are tested, correct, and expressive:

- **`BlockingQueue`** (`ArrayBlockingQueue`, `LinkedBlockingQueue`) — producer /
  consumer with blocking `put`/`take` and optional capacity.
- **`CountDownLatch`** — wait for one or more events.
- **`Semaphore`**, **`CyclicBarrier`**, **`Phaser`**, `ConcurrentHashMap`, etc.

Write with these and you avoid whole classes of subtle liveness bugs.

**Sample:** `BadWaitNotify` hand-rolls a bounded buffer with `wait`/`notifyAll`.
`GoodUtility` does the same with an `ArrayBlockingQueue` — smaller, and correct
by construction. `ConcurrencyUtilityTest` runs 4 producers × 8 consumers through
the good queue and checks the exact total, plus exercises the hand-rolled
version's basic ops.

---

## Item 82 — Document thread safety

Every class that holds mutable shared state must state its **thread-safety
level** in its Javadoc, or callers cannot use it safely. Common levels:

- **Immutable** — safe to share freely, no synchronization needed.
- **Unconditionally thread-safe** — safe concurrently with no external locking
  (e.g. `ConcurrentHashMap`).
- **Conditionally thread-safe** — safe for individual operations, but compound
  sequences (check-then-act) need external locking; *document which*.
- **Not thread-safe** — callers must synchronize externally; say so.
- **Thread-hostile** — never call concurrently; avoid.

Also document exactly what is and isn't atomic so callers don't build their own
races on compound operations.

**Sample:** `BadThreadDoc` is a `HashMap`-backed cache with *no* thread-safety
statement at all — a silent corruption trap. `GoodThreadDoc` is backed by
`ConcurrentHashMap`, states it is thread-safe, calls out that compound
read-modify-write sequences are *not* atomic, and advertises the atomic
`putIfAbsent`. `ThreadSafetyDocTest` verifies the concurrent-safe ops.

---

## Item 83 — Use lazy initialization judiciously

Lazy initialization (build the field on first use) saves work but costs
complexity and adds failure modes; do it only when it actually saves meaningful
cost (e.g. an expensive object or field that may never be used). If you do, get
the concurrency right:

- **Cross-thread (shared, lazily initialized field):** make the field
  `volatile` and use the *double-checked locking* idiom — or for a **static**
  field use the *holder class* idiom, which is correct for free because the JVM
  synchronizes class initialization.
- A *non-volatile* non-synchronized lazy write is the classic "half-built
  object" race: another thread may see a non-null-but-uninitialized instance.

**Sample:** `BadLazyInit` has a plain (non-`volatile`) unsynchronized lazy
field — a race. `GoodLazyInit` uses correct double-checked locking on a
`volatile` field; `GoodLazyHolder` shows the holder-class idiom for a static
singleton. `LazyInitTest` confirms both good patterns initialize once and hand
back the same instance.

---

## Item 84 — Don't depend on the thread scheduler

Never write code whose *correctness* relies on thread priorities, `yield()`,
`sleep()`-tuned timing, or "best guess" about scheduling. Such code is fragile,
hard to reason about, and fails in surprising ways under load or on a different
JVM/OS. The guiding principles:

- Prefer the concurrency utilities; they coordinate threads by *state events*
  (latches, queues, futures), not by timing.
- Never busy-wait or spin on a flag — it burns CPU and races with the scheduler.
- Always provide a **timeout** where you wait, so you fail cleanly instead of
  hanging on a thread that never runs.

**Sample:** `BadBusyWait` spins with `Thread.yield()` in a `while (!done)` loop —
CPU-wasteful and timing-dependent. `GoodSchedulerNeutral` uses a `CountDownLatch`
with a timeout. `SchedulerTest` shows the latch completes when given enough
time and *fails cleanly* (returns `false`) when the timeout is too short —
never a hang, never scheduler-dependent.

---

## Senior checklist

- [ ] Shared mutable state synchronized (or `volatile`/atomic as appropriate); no
      lost-update races (78).
- [ ] Lock scope minimal; no alien calls (listeners) made while holding a lock (79).
- [ ] Executors/`Future`/streams used instead of ad-hoc threads; clean bounded
      shutdown; results collected via `Future` (80).
- [ ] Higher-level `java.util.concurrent` utilities used instead of hand-rolled
      `wait`/`notify` (81).
- [ ] Every mutable-shared-state class documents its thread-safety level and what
      callers must still guard (82).
- [ ] Lazy initialization only when it pays, and implemented correctly
      (`volatile` DCL or holder class); no unsynchronized lazy writes (83).
- [ ] No reliance on the thread scheduler, `yield`, or timing tweaks; waits have
      timeouts and fail cleanly (84).
