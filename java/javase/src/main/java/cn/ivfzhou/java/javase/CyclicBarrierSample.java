package cn.ivfzhou.java.javase;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

// 让一组线程互相等待，直到所有线程都到达一个共同的屏障点后，再继续执行。
public final class CyclicBarrierSample {

    static void main(String[] args) {
        test();
    }

    public static void test() {
        final var cyclicBarrier = new CyclicBarrier(2);

        new Thread(() -> {
            try {
                System.out.println("1 wait");
                Thread.sleep(5000);
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("1 done");
        }).start();

        new Thread(() -> {
            try {
                System.out.println("2 wait");
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("2 done");
        }).start();
    }

}
