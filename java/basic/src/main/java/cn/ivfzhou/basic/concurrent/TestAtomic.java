package cn.ivfzhou.basic.concurrent;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

// 原子操作示例。
public final class TestAtomic {

    // 原子计数器。
    private static final LongAdder count = new LongAdder();

    // 原子数。
    private static final AtomicLong count1 = new AtomicLong();
    // 循环次数。
    private static final int total = 100000;
    // 普通数。
    private static long count2;

    public static long testCount() {
        var start = System.currentTimeMillis();
        for (var i = 0; i < total; i++) {
            new Thread(count::increment).start();
        }
        return System.currentTimeMillis() - start;
    }

    public static long testCount1() {
        var start = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            new Thread(count1::incrementAndGet).start();
        }
        return System.currentTimeMillis() - start;
    }

    public static long testCount2() {
        var start = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            new Thread(() -> {
                synchronized (TestAtomic.class) {
                    count2++;
                }
            }).start();
        }
        return System.currentTimeMillis() - start;
    }

    public static String string() {
        return count + "-" + count1 + "-" + count2;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(TestAtomic.testCount());
        System.out.println(TestAtomic.testCount1());
        System.out.println(TestAtomic.testCount2());
        Thread.sleep(5000L);
        System.out.println(TestAtomic.string());
    }

}
