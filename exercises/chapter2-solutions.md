# Chapter 2 — Solutions (Items 1–2)

## 1. Three many-parameter patterns

**Telescoping constructors**
- Defect: unreadable, position-bound, error-prone calls; scales with the
  number of parameters.
- Real bug: two same-typed parameters swapped silently, e.g.
  `new User("Han", "123-456", "han@x.com")` — because *email* and *phone* bind
  by position, the caller's intent is lost and the bug compiles clean,
  surfacing as corrupted data later.

**JavaBeans setters**
- Defect: object is observable in an inconsistent state between setters, is
  mutable forever, and enforces no invariants.
- Real bug: a caller forgets one setter (or a concurrent thread reads between
  setters — see chapter 10) and the object is used half-built; nothing marks
  it "ready".

**Builder**
- Defect: more code and wordier call sites; the only real cost.
- Real bug avoided: impossible to swap named args, impossible to forget
  validation, immutable product.

## 2. Why JavaBeans can't win

The JavaBeans pattern leaves the `setters` public on the *product*: after
construction anyone can call
`pizza.setCheese(false)` — the object's state is reachable and changeable, so
it cannot be immutable and cannot guarantee invariants.

The builder puts setters (`email(...)`, `admin(...)`) on the *builder*, which
is discarded after `build()`; the product has only final fields and no
mutators. Mutable configuration happens only during the disposable build
phase; what survives is immutable by construction.

## 3. Decisions

a) **Plain constructor** — two obvious params, no shape, no rules; a builder
   or record adds noise. (Or a record, equally fine.)
b) **Builder** — several optional fields with defaults and invariants
   (`port`, pools, counts); the builder expresses "only set what differs".
c) **record** (or plain immutable class) — fixed canonical shape, shared
   safely, zero boilerplate.
d) **record/immutable class** — values change by *creating new instances*
   rather than mutating, so sharing can't be corrupted by another holder.

## 4. Rewrite

```java
import java.util.Objects;

public final class ServerConfig {
    private final String host;
    private final int port;
    private final boolean secure;
    private final int maxConnections;

    private ServerConfig(Builder builder) {
        this.host = Objects.requireNonNull(builder.host, "host");
        this.port = builder.port;
        this.secure = builder.secure;
        this.maxConnections = builder.maxConnections;
    }

    public static Builder builder(String host) { return new Builder(host); }

    public String host() { return host; }
    public int port() { return port; }
    public boolean secure() { return secure; }
    public int maxConnections() { return maxConnections; }

    public static final class Builder {
        private final String host;
        private int port = 8080;
        private boolean secure;
        private int maxConnections = 10;

        private Builder(String host) { this.host = host; }

        public Builder port(int port) {
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("port out of range: " + port);
            }
            this.port = port;
            return this;
        }

        public Builder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder maxConnections(int maxConnections) {
            if (maxConnections < 1) {
                throw new IllegalArgumentException(
                        "maxConnections must be >= 1: " + maxConnections);
            }
            this.maxConnections = maxConnections;
            return this;
        }

        public ServerConfig build() { return new ServerConfig(this); }
    }
}
```

What improved: required `host` enforced in the factory; `port` and
`maxConnections` validated the moment they are set (fail-fast); defaults
apply until overridden; the built object is fully immutable.

## 5. Spot the defect

`CoffeeBuilder` never overrides `protected abstract T self();`. Because
`Drink.Builder.Builder` declares `self()` abstract, `CoffeeBuilder` stays
**abstract** — `new CoffeeBuilder()` will not compile. The fix is to override
it to return the concrete builder:

```java
@Override protected CoffeeBuilder self() { return this; }
```

The invariants are the pair `T extends Builder<T>` + `protected abstract T
self()`: the recursive bound keeps the chain typed, and each leaf builder
must supply `self()` (= `this`) so fluent calls keep returning the most
specific type. Forgetting either breaks compilation of the subclass or
silently degrades the chain to the base builder type.

## 6. Fail-fast at the factory

`Objects.requireNonNull` turns domain corruption into an *immediate, located*
error at the boundary — the stack trace names the exact call that passed
`null`. If validation is deferred to first use, the NPE (or worse, a silent
wrong default) surfaces in an unrelated caller far from the cause, and the
message is useless. The same fail-early principle applies to: `build()`
verifying invariants, `Objects.checkIndex` on array bounds, and validating
configuration before starting threads.

## 7. Singleton choice

The three idioms: public static final field, static factory
(`getInstance()`), and enum constant. Guarantees:

- **Public field / static factory** — exactly one instance only if nothing
  abuses reflection (`setAccessible(true)` on the private constructor can
  mint a second one) and only if deserialization is handled
  (`Serializable` classes must add `readResolve()` that returns the
  singleton, otherwise deserialization creates a new instance).
- **Enum** — the JVM rejects reflective creation of enum constants
  (`IllegalArgumentException: Cannot reflectively create enum objects`) and
  serialization of an enum always resolves back to the existing constant.
  Reflection-safe and serialization-safe by construction.

For `MetricsRegistry` you **ship the enum**: `public enum
MetricsRegistry { INSTANCE; ... }`. One line, and both threats (serialization
round-trips, reflective bootstrap) are neutralised without extra
`readResolve`/reflection hacks. If you had to keep the field idiom, you would
add `private Object readResolve() { return INSTANCE; }`.

## 8. Harden the utility class

What is wrong: declaring a utility class without *any* constructor yields a
default **public** no-arg constructor, so `new Strings()` compiles and works —
a utility that shouldn't be instantiated, instantiated silently.

```java
public final class Strings {
    private Strings() {
        throw new AssertionError("No instances");
    }
    public static String normalize(String s) {
        return s.trim().toLowerCase();
    }
}
```

`final` blocks subclassing; the private constructor blocks external `new`;
the `AssertionError` makes an *internal* accidental `new Strings()` inside
the class itself fail loudly instead of silently producing a useless object.

## 9. DI refactor

```java
public final class InvoicingService {
    private final InvoiceStore store;
    public InvoicingService(InvoiceStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }
    public String issue(String id) { return store.save(id); }
}

public interface InvoiceStore { String save(String id); }
```

Test:

```java
InvoiceStore fake = id -> "fake:" + id;
InvoicingService service = new InvoicingService(fake);
assertEquals("fake:123", service.issue("123"));
```

The assertion passes *only if* the service routes through the injected fake —
proving the dependency is actually used and swappable, which hardwired
`new DbInvoiceStore()` can never show.

## 10. Regex reuse + balance

```java
private static final Pattern DATE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
// DATE.matcher(value).matches()
```

`String.matches` compiles a new `Pattern` on every invocation; hoisting it to
a `static final` field means the pattern is compiled once.

The other direction: *cheap* per-call objects should not be pooled
(premature reuse). Example: a tiny immutable value like `LocalDate.of(y,m,d)`
or a one-shot collector in a stream — constructing them is trivial and the
JIT can scalar-replace them; a hand-written pool adds retention risk
(item 7), thread-safety cost, and bug surface for nothing. Item 6 cuts both
ways: hoist *expensive* construction, don't hoist *cheap allocation*.

## 11. Retained references

The queue's backing array (or list) keeps strong references to every item
ever enqueued — `dequeue` lowers the logical size but never nulls the slot,
so the objects stay *reachable* and the GC can't collect them. Over months
that builds into OOM even though the queue is logically empty.

The fix: when dequeuing, clear the slot —
`elements[--size] = null;` — exactly what `GoodStack.pop()` does (the tests
read the array reflectively to show bad keeps `"b"` and good has `null`).

Finding it: heap dump via `jmap -dump`, inspected with MAT/JProfiler —
look for a class with an implausibly large dominant-retained size, then walk
the "Path to GC roots" from the retained instances back to a queue/array
field.

Other hotspots: caches that never evict (use `WeakHashMap`/expiry), and
listener/callback registries — a `subscribe` without an `unsubscribe` pins
both the listener and the host for the JVM's lifetime.

## 12. Finalizer replacement

`finalize()` is unreliable: it runs "sometime, maybe never" at the GC's
whim, there is no ordering guarantee, it slows GC for every finalizable
object, and after Java 9 it's deprecated. A leak/connection that is "cleaned"
by finalization is cleaned *eventually and unpredictably* — the JVM can exit
before it runs.

```java
public final class DatabaseHandle implements AutoCloseable {
    private boolean closed;
    public void query(String sql) {
        if (closed) throw new IllegalStateException("handle is closed");
        /* use native peer */
    }
    @Override public void close() { if (!closed) { disconnect(); closed = true; } }
}
```

Ownership is explicit: the caller closes in a try-with-resources block
(item 9); `use()`/`query()` guards against use-after-close; and the class
does not depend on GC to release the native peer. `Cleaner` only as a
backstop if the class also owns memory beyond the heap.

## 13. Try-with-resources

With `try/finally`: the `finally`'s `close()` throws "close boom" *during*
exception handling, so the "read boom" from the body is **overwritten**.
The caller sees only "close boom"; the primary failure is gone.

With try-with-resources: the body's "read boom" propagates, and the close
failure is recorded as a **suppressed** exception on it. The caller reads
`exception.getSuppressed()` to see "close boom" — nothing is lost.

Multiple resources close in **reverse order** of declaration, and every
declared resource is still closed even if an earlier one failed.