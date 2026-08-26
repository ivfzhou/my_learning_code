package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.agent.Agent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.middleware.MiddlewareBase;
import io.agentscope.core.middleware.ModelCallInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class RateLimitMiddleware implements MiddlewareBase {

    private final long minIntervalMs;
    private final AtomicLong lastCall = new AtomicLong(0);

    public RateLimitMiddleware(Duration minInterval) {
        this.minIntervalMs = minInterval.toMillis();
    }

    @Override
    public Flux<AgentEvent> onModelCall(Agent agent, RuntimeContext ctx, ModelCallInput input, Function<ModelCallInput, Flux<AgentEvent>> next) {
        long now = System.currentTimeMillis();
        long wait = minIntervalMs - (now - lastCall.get());
        Mono<Void> delay = wait > 0 ? Mono.delay(Duration.ofMillis(wait)).then() : Mono.empty();
        return delay.thenMany(next.apply(input)).doOnSubscribe(s -> lastCall.set(System.currentTimeMillis()));
    }

}
