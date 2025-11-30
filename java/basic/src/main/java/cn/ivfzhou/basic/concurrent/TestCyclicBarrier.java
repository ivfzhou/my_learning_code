package cn.ivfzhou.basic.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

// 让一组线程互相等待，直到所有线程都到达一个共同的屏障点后，再继续执行。
public final class TestCyclicBarrier {

    public static void test() {
        final var cb = new CyclicBarrier(2);
        new Thread(() -> {
            try {
                System.out.println("1 wait");
                Thread.sleep(5000);
                cb.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("1 done");
        }).start();
        new Thread(() -> {
            try {
                System.out.println("2 wait");
                cb.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("2 done");
        }).start();
    }

    public static void main(String[] args) {
        test();
    }

}
