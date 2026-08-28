package cn.ivfzhou.java.flux;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

public final class Sample {

    static void main() throws InterruptedException {
        handleError();
    }

    private static void createAndSubscribe() {
        Flux.just("Apple", "Banana", "Orange", "Pear")
                .subscribe(
                        v -> System.out.println("received " + v),
                        err -> System.out.println("error " + err),
                        () -> System.out.println("done")
                );

        Flux.fromIterable(List.of("Apple", "Banana", "Orange", "Pear"))
                .subscribe(
                        v -> System.out.println("received " + v),
                        err -> System.out.println("error " + err),
                        () -> System.out.println("done")
                );

        Flux.range(1, 10)
                .subscribe(
                        v -> System.out.println("received " + v),
                        err -> System.out.println("error " + err),
                        () -> System.out.println("done")
                );
    }

    private static void transform() {
        Flux.just("reactor", "java", "stream")
                .map(String::toUpperCase)
                .filter(v -> v.length() > 4)
                .flatMap(v -> Flux.fromArray(v.split("")))
                .distinct()
                .sort()
                .buffer(3) // 每 3 个字符打包成一个 List。
                .subscribe(System.out::println);
    }

    private static void asynchronous() throws InterruptedException {
        Flux.interval(Duration.ofSeconds(1))
                .take(5) // 订阅并打印前 5 个，然后取消。
                .subscribe(
                        v -> System.out.println("received " + v),
                        err -> System.out.println("error " + err),
                        () -> System.out.println("done")
                );
        Thread.sleep(5000L);
    }

    private static void backpressure() throws InterruptedException {
        Flux.interval(Duration.ofMillis(10))
                .onBackpressureBuffer(20) // 最多缓冲 20 个，超过后抛出 BufferOverflowException。
                .publishOn(Schedulers.boundedElastic()) // 切换到有界弹性线程池。
                .limitRate(5) // 每次请求的批量大小。
                .subscribe(
                        v -> {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            System.out.println("received " + v);
                        },
                        err -> System.out.println("error " + err),
                        () -> System.out.println("done")
                );
        Thread.sleep(5000);
    }

    private static void handleError() {
        Flux.range(1, 10)
                .map(v -> {
                    if (v == 3) {
                        throw new RuntimeException("error occurred on 3");
                    }
                    return v * 10;
                })
                .onErrorReturn(-1) // 发生错误时返回一个固定值。
                .subscribe(
                        v -> System.out.println("received " + v),
                        err -> System.out.println("error " + err),
                        () -> System.out.println("done")
                );

        Flux.range(1, 10)
                .map(v -> {
                    if (v == 3) {
                        throw new RuntimeException("error occurred on 3");
                    }
                    return v * 10;
                })
                .onErrorResume(v -> Flux.just(100, 200, 300)) // 发生错误时用另一个流替代。
                .subscribe(
                        v -> System.out.println("received " + v),
                        err -> System.out.println("error " + err),
                        () -> System.out.println("done")
                );

        Flux.range(1, 10)
                .map(v -> {
                    if (v == 3) {
                        throw new RuntimeException("error occurred on 3");
                    }
                    return v * 10;
                })
                .retry(2) // 重试 2 次，仍会失败，最终错误传播。
                .subscribe(
                        v -> System.out.println("received " + v),
                        err -> System.out.println("error " + err),
                        () -> System.out.println("done")
                );
    }

}
