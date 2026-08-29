package cn.ivfzhou.java.javase;

import java.util.concurrent.Semaphore;

public final class SemaphoreSample {

    static void main(String[] args) throws InterruptedException {
        test();
    }

    public static void test() throws InterruptedException {
        var semaphore = new Semaphore(2);

        new Thread(() -> {
            try {
                System.out.println("1");
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1);
            semaphore.release();
        }).start();

        new Thread(() -> {
            try {
                System.out.println("2");
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(2);
            semaphore.release();
        }).start();

        new Thread(() -> {
            try {
                System.out.println("3");
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(3);
            semaphore.release();
        }).start();

        semaphore.acquire();
        semaphore.release();
    }

}
