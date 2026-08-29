package cn.ivfzhou.java.javase;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadSample {

    static void main(String[] args) throws InterruptedException {
        test();
        test2();
        Thread.sleep(1000);
    }

    private static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("重写run方法");
        }
    }

    private static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("实现run方法");
        }
    }

    static void test() {
        var thread = new MyThread();
        thread.start();
    }

    static void test2() {
        var thread = new Thread(new MyRunnable());
        thread.start();
    }

    static void test3() throws ExecutionException, InterruptedException {
        var pool = new ThreadPoolExecutor(
                10,
                20,
                1000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        var res = pool.submit(() -> "callable");
        System.out.println(res.get());
        pool.shutdown();
    }

}
