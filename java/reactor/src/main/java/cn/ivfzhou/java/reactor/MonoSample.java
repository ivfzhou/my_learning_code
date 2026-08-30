package cn.ivfzhou.java.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;

public class MonoSample {

    static void main() {
        exchange();
    }

    private static void create() {
        Mono.just("hello 1").subscribe(System.out::println);

        // 空 Mono（不发射任何元素，直接完成）。
        Mono.empty().subscribe(
                v -> System.out.println("hello " + v),
                err -> System.out.println(err.getMessage()),
                () -> System.out.println("done 2")
        );

        Mono.fromCallable(() -> "hello 3").subscribe(System.out::println);

        Mono.fromFuture(() -> CompletableFuture.completedFuture("hello 4")).subscribe(System.out::println);

        Mono.fromCompletionStage(() -> CompletableFuture.completedFuture("hello 5")).subscribe(System.out::println);

        Mono.error(new RuntimeException("error 6")).subscribe(
                System.out::println,
                err -> System.out.println(err.getMessage()),
                () -> System.out.println("done")
        );

        // 延迟创建，每次订阅都重新执行。
        Mono.defer(() -> Mono.just("hello 7")).subscribe(System.out::println);
    }

    private static void operate() {
        Mono.just("hello").map(x -> x + " 1").subscribe(System.out::println);

        Mono.just("hello").flatMap(x -> Mono.just(x + " 2")).subscribe(System.out::println);

        Mono.just(1).filter(x -> x > 0).subscribe(System.out::println);
        Mono.just(-1).filter(x -> x > 0).subscribe(
                System.out::println,
                System.out::println,
                () -> System.out.println("done 3")
        );

        Mono.empty().defaultIfEmpty("hello 4").subscribe(System.out::println);
    }

    private static void handleError() {
        Mono.error(new RuntimeException("error")).onErrorReturn("recovery").subscribe(System.out::println);

        Mono.error(new RuntimeException("error")).onErrorResume(err -> Mono.just("recovery")).subscribe(System.out::println);
    }

    private static void combination() {
        Mono.zip(Mono.just(1), Mono.just(2)).subscribe(System.out::println);

        // 忽略前一个结果，继续另一个 Mono。
        Mono.just(1).then(Mono.just(2)).subscribe(System.out::println);
    }

    private static void result() {
        System.out.println(Mono.just(1).block()); //  // 阻塞当前线程，等待结果。
    }

    private static void exchange() {
        Mono.just(1).flux().subscribe(System.out::println);

        // 取第一个元素。
        Flux.just(1, 2).next().subscribe(System.out::println);

        Flux.just(1, 2).collectList().subscribe(System.out::println);
    }

    private static void threadPool() {
        Mono.fromCallable(() -> "hello 1")
                .subscribeOn(Schedulers.boundedElastic()) // 让上面的 Callable 在 boundedElastic 线程执行。
                .subscribe(System.out::println);
    }

}
