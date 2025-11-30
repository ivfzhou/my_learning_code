package cn.ivfzhou.basic.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class TestThreadPool {

    static void test() throws ExecutionException, InterruptedException {
        var pool = new ThreadPoolExecutor(
                10,
                20,
                1000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        Future<String> res = pool.submit(() -> "callable");
        System.out.println(res.get());
        pool.shutdown();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        test();
    }

}
