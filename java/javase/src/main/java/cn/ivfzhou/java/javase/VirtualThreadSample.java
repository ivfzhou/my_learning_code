package cn.ivfzhou.java.javase;

import java.time.Duration;
import java.util.concurrent.Executors;

public final class VirtualThreadSample {

    static void main() throws InterruptedException {
        scopedValue();
    }

    private static void createAndRun() throws InterruptedException {
        var thread = Thread.ofVirtual()
                .name("demo")
                .start(() -> System.out.println("running " + Thread.currentThread()));
        thread.join();

        var factory = Thread.ofVirtual().factory();
        var thread1 = factory.newThread(() -> System.out.println("a running " + Thread.currentThread()));
        var thread2 = factory.newThread(() -> System.out.println("b running " + Thread.currentThread()));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        var executor = Executors.newVirtualThreadPerTaskExecutor();
        try (executor) {
            executor.submit(() -> System.out.println("c running " + Thread.currentThread()));
            executor.submit(() -> System.out.println("d running " + Thread.currentThread()));
        } // 自动关闭并等待所有任务完成。
    }

    private static void millionVirtualThread() {
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        var startTimestamp = System.currentTimeMillis();
        try (executor) {
            for (long i = 0; i < 1_000_000; i++) {
                final var taskId = i;
                executor.submit(() -> {
                    try {
                        Thread.sleep(100);
                        System.out.println("done " + taskId);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        var endTimestamp = System.currentTimeMillis();
        System.out.println("time cost " + Duration.ofMillis(endTimestamp - startTimestamp));
    }

    private static void compare() {
        final var taskCount = 10_000;

        var executor = Executors.newVirtualThreadPerTaskExecutor();
        var startTimestamp = System.currentTimeMillis();
        try (executor) {
            for (long i = 0; i < taskCount; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        var endTimestamp = System.currentTimeMillis();
        System.out.println("virtual time cost " + Duration.ofMillis(endTimestamp - startTimestamp));

        var executors2 = Executors.newFixedThreadPool(200);
        var startTimestamp2 = System.currentTimeMillis();
        try (executors2) {
            for (long i = 0; i < taskCount; i++) {
                executors2.submit(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        var endTimestamp2 = System.currentTimeMillis();
        System.out.println("platform time cost " + Duration.ofMillis(endTimestamp2 - startTimestamp2));
    }

    private static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

    private static void scopedValue() {
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        try (executor) {
            for (long i = 0; i < 5; i++) {
                final var taskId = i;
                executor.submit(() -> ScopedValue.where(USER_ID, "userId-" + taskId).run(VirtualThreadSample::process));
            }
        }
    }

    private static void process() {
        var userId = USER_ID.get();
        System.out.println("userId " + userId);

        if ("userId-3".equals(userId)) {
            var thread = Thread.ofVirtual().start(() -> System.out.println("child thread userId "
                    + (USER_ID.isBound() ? USER_ID.get() : null)));
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
