package cn.ivfzhou.java.javase;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public final class AtomicSample {

    static void main(String[] args) throws InterruptedException {
        count();
        count1();
        count2();
        System.out.println("count " + count.longValue());
        System.out.println("count1 " + count1.longValue());
        System.out.println("count2 " + count2);
    }

    private static final int total = 100000;

    private static final LongAdder count = new LongAdder();

    private static final AtomicLong count1 = new AtomicLong();

    private static long count2;

    public static void count() {
        var start = System.currentTimeMillis();
        for (var i = 0; i < total; i++) {
            new Thread(count::increment).start();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    public static void count1() {
        var start = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            new Thread(count1::incrementAndGet).start();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    public static void count2() {
        var start = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            new Thread(() -> {
                synchronized (AtomicSample.class) {
                    count2++;
                }
            }).start();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

}
