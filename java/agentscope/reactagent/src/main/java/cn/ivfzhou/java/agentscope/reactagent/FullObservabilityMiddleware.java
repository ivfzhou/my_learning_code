package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.agent.Agent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.middleware.ActingInput;
import io.agentscope.core.middleware.AgentInput;
import io.agentscope.core.middleware.MiddlewareBase;
import io.agentscope.core.middleware.ModelCallInput;
import io.agentscope.core.middleware.ReasoningInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class FullObservabilityMiddleware implements MiddlewareBase {

    @Override
    public Flux<AgentEvent> onAgent(Agent agent, RuntimeContext ctx, AgentInput input, Function<AgentInput, Flux<AgentEvent>> next) {
        System.out.println("[onAgentBegin] name=" + agent.getName());
        return next.apply(input).doOnComplete(() -> System.out.println("[onAgentEnd] name=" + agent.getName()));
    }

    @Override
    public Flux<AgentEvent> onReasoning(Agent agent, RuntimeContext ctx, ReasoningInput input, Function<ReasoningInput, Flux<AgentEvent>> next) {
        System.out.println("[onReasoningBegin]");
        return next.apply(input).doOnComplete(() -> System.out.println("[onReasoningEnd]"));
    }

    @Override
    public Flux<AgentEvent> onActing(Agent agent, RuntimeContext ctx, ActingInput input, Function<ActingInput, Flux<AgentEvent>> next) {
        System.out.println("[onActingBegin]");
        return next.apply(input).doOnComplete(() -> System.out.println("[onActingEnd]"));
    }

    @Override
    public Flux<AgentEvent> onModelCall(Agent agent, RuntimeContext ctx, ModelCallInput input, Function<ModelCallInput, Flux<AgentEvent>> next) {
        System.out.println("[onModelCall] classSimapleName=" + input.model().getClass().getSimpleName());
        return next.apply(input).doOnComplete(() -> System.out.println("[onModelCallEnd]"));
    }

    @Override
    public Mono<String> onSystemPrompt(Agent agent, RuntimeContext ctx, String currentPrompt) {
        System.out.println("[onSystemPrompt] length=" + currentPrompt.length());
        return Mono.just(currentPrompt);
    }

    @Override
    public int order() {
        return 0;
    }

}
