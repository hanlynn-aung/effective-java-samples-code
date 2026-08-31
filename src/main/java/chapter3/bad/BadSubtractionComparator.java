package chapter3.bad;

import java.util.Comparator;

public final class BadSubtractionComparator
        implements Comparator<BadSubtractionComparator.Item> {

    public static final class Item {
        public final int score;

        public Item(int score) {
            this.score = score;
        }
    }

    @Override
    public int compare(Item a, Item b) {
        return a.score - b.score;
    }
}