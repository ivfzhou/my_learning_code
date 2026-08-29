package cn.ivfzhou.java.javase;

import java.util.concurrent.Phaser;

public final class PhaserSample {

    static void main(String[] args) {
        test();
    }

    public static void test() {
        final var phaser = new Phaser(2);

        new Thread(() -> {
            System.out.println("1");
            phaser.arriveAndAwaitAdvance();
            System.out.println("1 done");
        }).start();

        new Thread(() -> {
            System.out.println("2");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            phaser.arriveAndAwaitAdvance();
            System.out.println("2 done");
        }).start();

        System.out.println("wait");
        phaser.awaitAdvance(2);
    }

}
