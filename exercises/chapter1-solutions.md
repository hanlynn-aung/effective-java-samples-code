# Chapter 1 — Solutions

## 1. The four benefits

1. **They have names** — `Point.cartesian(x, y)` vs `Point.polar(r, theta)`
   document the difference that `new Point(...)` hides.
2. **No new object required (instance control)** — `Boolean.valueOf(false)`
   returns one of two pre-existing instances; a cache can serve the same
   instance for equal inputs.
3. **Can return any subtype** — the declared return type can be an interface
   while the concrete class stays private (JDBC `DriverManager.getConnection`
   returns whatever driver implementation is registered).
4. **Returned type can vary by input or evolve** — `secure(address)` returns a
   `SecureConnection`, `plain(address)` a `PlainConnection`; new
   implementations can be added later without breaking callers.

## 2. Instance control

a) The class must be **immutable** (all fields private and final, no
   mutators — see chapter 3).

b) If a shared, mutable instance is handed to two callers, one caller's
   mutation leaks into the other through the shared object (aliasing). With
   identity being the only difference, aliasing silently corrupts state and
   is extremely hard to debug. Immutable objects have no mutation to leak, so
   sharing is safe — which is why `String`, `Integer`, `LocalDate` share
   instances freely.

## 3. Returning subtypes

a) A public constructor hard-codes the class the caller instantiates — you
   `new` the exact type. To return a hidden implementation you need a method
   whose *declared* return type is broader than the object it actually
   constructs. Constructors can't have an abstract/narrower "declared" type;
   factories can.

b) *Service-provider frameworks*: the factory is the fixed entry point
   (e.g. `Connection.get(id)` / `ServiceLoader.load(Spi.class)`); the service
   implementation is looked up and returned at runtime, so the library can
   evolve its internals — or the container can choose the implementation —
   without callers changing a character.

## 4. When the constructor is right

- **One obvious, self-named construction** — `new StringBuilder()` needs no
  name; a factory would add ceremony for nothing.
- **No instance control or subtype benefit needed, and the class is designed
  for subclassing** — a hidden constructor blocks `extends` for external
  subclasses (usually intended, but if not, keep a public/protected
  constructor).
- Also fine: simple value objects where a single constructor + `Objects
  .requireNonNull` is already clear, and Java records (the record's
  canonical constructor).

## 5. Rewrite

```java
import java.util.Objects;

public final class Order {
    private final String customer;
    private final boolean temporary;

    private Order(String customer, boolean temporary) {
        this.customer = Objects.requireNonNull(customer, "customer");
        this.temporary = temporary;
    }

    public static Order forCustomer(String customer) {
        return new Order(customer, false);
    }

    public static Order temporary(String customer) {
        return new Order(customer, true);
    }

    public String customer() { return customer; }
    public boolean temporary() { return temporary; }
}
```

What improved: two *named* factories replace two ambiguous constructors;
the constructor is private so every creation path is validated; `null`
fails fast at the boundary; two self-explanatory call sites
(`Order.forCustomer(x)` vs `Order.temporary(x)`).

## 6. Boxing identity

`valueOf` caches instances for `-128..127` (per the JVM spec's preferred
range). Inside the range the cache returns the *same* boxed object, so `==`
(identity) happens to be `true`; outside the range a fresh object is made
each call, so `==` is `false`. `==` on boxed values is therefore a coin flip
depending on range and implementation — never rely on or reason from it.
Always compare boxes with `.equals()` (or unbox to primitives) so you compare
*values*, not identity. The cache is a performance detail; the identity result
is undefined behaviour you must not program against.