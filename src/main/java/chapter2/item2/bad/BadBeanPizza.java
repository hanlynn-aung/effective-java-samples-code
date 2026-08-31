package chapter2.item2.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadBeanPizza {
    private String size = "medium";
    private boolean cheese = true;
    private final List<String> toppings = new ArrayList<>();

    public BadBeanPizza() { }

    public void setSize(String size) { this.size = size; }
    public void setCheese(boolean cheese) { this.cheese = cheese; }

    public String size() { return size; }
    public boolean cheese() { return cheese; }

    public void addTopping(String topping) { toppings.add(topping); }
    public List<String> toppings() { return List.copyOf(toppings); }
}