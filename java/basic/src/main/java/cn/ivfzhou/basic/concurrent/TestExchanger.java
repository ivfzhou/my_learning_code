package cn.ivfzhou.basic.concurrent;

import java.util.concurrent.Exchanger;

public final class TestExchanger {

    public static void test() {
        var e = new Exchanger<String>();
        new Thread(() -> {
            try {
                System.out.println("1: " + e.exchange("1"));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                System.out.println("2: " + e.exchange("2"));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws InterruptedException {
        test();
        Thread.sleep(1000);
    }

}
