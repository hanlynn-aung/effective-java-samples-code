package chapter11.good;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class GoodExecutor {

    public int runPyramid(int levels) throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(4);
        List<Future<Integer>> results = new LinkedList<>();
        try {
            for (int i = 0; i < levels; i++) {
                final int value = i;
                Callable<Integer> task = () -> value * value;
                results.add(exec.submit(task));
            }
        } finally {
            exec.shutdown();
        }
        exec.awaitTermination(10, TimeUnit.SECONDS);
        int sum = 0;
        for (Future<Integer> f : results) {
            sum += f.get();
        }
        return sum;
    }
}