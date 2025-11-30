package cn.ivfzhou.basic.concurrent;

import java.util.concurrent.CountDownLatch;

// 等待所有线程退出。
public final class TestCountDownLatch {

    public static void test1() throws InterruptedException {
        var cdl = new CountDownLatch(2);
        new Thread(() -> {
            System.out.println("1 done");
            cdl.countDown();
        }).start();
        new Thread(() -> {
            System.out.println("2 done");
            cdl.countDown();
        }).start();
        cdl.await();
    }

    public static void test2() throws InterruptedException {
        var thread = new Thread(() -> {
            System.out.println("1 done");
        });
        var thread1 = new Thread(() -> {
            System.out.println("2 done");
        });
        thread.start();
        thread1.start();
        thread1.join();
        thread.join();
    }

    public static void main(String[] args) throws InterruptedException {
        test2();
    }

}
