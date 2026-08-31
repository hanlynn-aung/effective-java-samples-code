package chapter2.item2.good;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class GoodPizza {
    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }

    final List<Topping> toppings;

    GoodPizza(Builder<?> builder) {
        this.toppings = builder.copyToppings();
    }

    public List<Topping> toppings() {
        return Collections.unmodifiableList(toppings);
    }

    public abstract static class Builder<T extends Builder<T>> {
        private final List<Topping> toppings = new ArrayList<>();

        protected abstract T self();

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping, "topping"));
            return self();
        }

        abstract GoodPizza build();

        private List<Topping> copyToppings() {
            return new ArrayList<>(toppings);
        }
    }
}