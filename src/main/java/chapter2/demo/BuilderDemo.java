package chapter2.demo;

import chapter2.item1.bad.BadStaticFactoryService;
import chapter2.item1.good.GoodStaticFactoryService;
import chapter2.item2.bad.BadBeanPizza;
import chapter2.item2.bad.BadTelescopingUser;
import chapter2.item2.good.GoodBuilderUser;
import chapter2.item2.good.GoodCalzone;
import chapter2.item2.good.GoodNyPizza;
import chapter2.item2.good.GoodPizza;

public final class BuilderDemo {
    public static void main(String[] args) {
        System.out.println("== Item 1: static factory ==");
        BadStaticFactoryService badService = new BadStaticFactoryService(null);
        System.out.println("bad: null endpoint accepted: "
                + (badService.endpoint() == null));
        try {
            GoodStaticFactoryService.connectedTo(null);
            System.out.println("good: null NOT rejected (unexpected)");
        } catch (NullPointerException e) {
            System.out.println("good: null rejected immediately: "
                    + e.getMessage());
        }

        System.out.println();
        System.out.println("== Item 2: telescoping constructors ==");
        BadTelescopingUser swapped = new BadTelescopingUser(
                "Han", "123-456", "han@example.com");
        System.out.println("caller meant (email=\"han@example.com\", "
                + "phone=\"123-456\") but got:");
        System.out.println("  email = " + swapped.email());
        System.out.println("  phone = " + swapped.phone());

        System.out.println();
        System.out.println("== Item 2: JavaBeans pattern ==");
        BadBeanPizza bean = new BadBeanPizza();
        System.out.println("fresh instance, before any setter: cheese defaults to "
                + bean.cheese());
        bean.setCheese(false);
        System.out.println("still mutable after 'construction': cheese now "
                + bean.cheese());

        System.out.println();
        System.out.println("== Item 2: builder ==");
        GoodBuilderUser user = GoodBuilderUser.builder("Han")
                .email("han@example.com")
                .admin(true)
                .build();
        System.out.printf("built user: name=%s email=%s admin=%s%n",
                user.name(), user.email(), user.admin());

        System.out.println();
        System.out.println("== Item 2: hierarchical builders ==");
        GoodNyPizza ny = new GoodNyPizza.Builder(GoodNyPizza.Size.LARGE)
                .addTopping(GoodPizza.Topping.HAM)
                .addTopping(GoodPizza.Topping.ONION)
                .build();
        System.out.printf("ny pizza: size=%s toppings=%s class=%s%n",
                ny.size(), ny.toppings(), ny.getClass().getSimpleName());

        GoodCalzone calzone = new GoodCalzone.Builder()
                .addTopping(GoodPizza.Topping.SAUSAGE)
                .sauceInside()
                .build();
        System.out.printf("calzone: sauceInside=%s toppings=%s class=%s%n",
                calzone.sauceInside(), calzone.toppings(),
                calzone.getClass().getSimpleName());
    }
}