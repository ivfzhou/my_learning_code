package cn.ivfzhou.basic.concurrent;

import java.util.concurrent.Phaser;

public final class TestPhaser {

    public static void test() {
        var p = new Phaser(2);
        new Thread(() -> {
            System.out.println("1");
            p.arriveAndAwaitAdvance();
            System.out.println("1 done");
        }).start();
        new Thread(() -> {
            System.out.println("2");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            p.arriveAndAwaitAdvance();
            System.out.println("2 done");
        }).start();
        System.out.println("wait");
        p.awaitAdvance(2);
    }

    public static void main(String[] args) {
        test();
    }

}
