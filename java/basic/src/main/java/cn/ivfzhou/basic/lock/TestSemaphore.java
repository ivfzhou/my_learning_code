package cn.ivfzhou.basic.lock;

import java.util.concurrent.Semaphore;

public final class TestSemaphore {

    public static void test() throws InterruptedException {
        var s = new Semaphore(2);
        new Thread(() -> {
            try {
                System.out.println("1");
                s.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1);
            s.release();
        }).start();
        new Thread(() -> {
            try {
                System.out.println("2");
                s.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(2);
            s.release();
        }).start();
        new Thread(() -> {
            try {
                System.out.println("3");
                s.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(3);
            s.release();
        }).start();
        s.acquire();
        s.release();
    }

    public static void main(String[] args) throws InterruptedException {
        test();
    }

}
