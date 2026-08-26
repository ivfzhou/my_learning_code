package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.agent.Agent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.middleware.MiddlewareBase;
import io.agentscope.core.middleware.ModelCallInput;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public class TimingMiddleware implements MiddlewareBase {

    @Override
    public Flux<AgentEvent> onModelCall(Agent agent, RuntimeContext ctx, ModelCallInput input, Function<ModelCallInput, Flux<AgentEvent>> next) {
        long start = System.nanoTime();
        return next.apply(input)
                .doFinally(sig -> {
                    long ms = (System.nanoTime() - start) / 1_000_000;
                    System.out.println("[timing] " + agent.getName() + ": " + ms + "ms");
                });
    }

}
