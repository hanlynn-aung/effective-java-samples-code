package chapter11.good;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class GoodUtility {

    private final BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(3);

    public void put(int value) throws InterruptedException {
        buffer.put(value);
    }

    public int take() throws InterruptedException {
        return buffer.take();
    }

    public int size() {
        return buffer.size();
    }
}