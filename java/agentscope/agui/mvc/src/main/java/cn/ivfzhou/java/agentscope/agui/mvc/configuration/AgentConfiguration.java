package cn.ivfzhou.java.agentscope.agui.mvc.configuration;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Agent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.agui.AguiException;
import io.agentscope.core.agui.adapter.AguiAdapterConfig;
import io.agentscope.core.agui.adapter.strategy.AgentEventConverter;
import io.agentscope.core.agui.adapter.strategy.AguiEventEnricher;
import io.agentscope.core.agui.adapter.strategy.AguiStreamContext;
import io.agentscope.core.agui.event.AguiEvent;
import io.agentscope.core.agui.event.AguiEvents;
import io.agentscope.core.agui.processor.AgentResolver;
import io.agentscope.core.agui.processor.AguiRequestProcessor;
import io.agentscope.core.agui.registry.AguiAgentRegistry;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.event.AgentStartEvent;
import io.agentscope.core.event.CustomEvent;
import io.agentscope.core.middleware.AgentInput;
import io.agentscope.core.middleware.MiddlewareBase;
import io.agentscope.core.model.Model;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.mcp.McpClientBuilder;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import io.agentscope.extensions.model.openai.formatter.OpenAIChatFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Configuration
public class AgentConfiguration {

    private final String defaultAgentId = "default";
    private final boolean emitStateEvents = true;
    private final boolean emitToolCallArgs = true;
    private final boolean emitTokenUsage = true;
    private final boolean enableReasoning = true;
    private final boolean emitRunFinishedAfterError = false;

    @Bean
    public AguiAgentRegistry aguiAgentRegistry() {
        var registry = new AguiAgentRegistry();
        // registry.registerFactory("default", this::defaultAgent);
        // registry.registerFactory("chat", this::chatAgent);
        registry.register("default", defaultAgent());
        registry.register("chat", chatAgent());
        return registry;
    }

    @Bean
    public AgentEventConverter agentEventConverter() {
        return new AgentEventConverter() {

            @Override
            public Set<Class<? extends AgentEvent>> eventTypes() {
                return Set.of(CustomEvent.class);
            }

            @Override
            public void convert(AgentEvent event, AguiStreamContext context) {
                if (event instanceof CustomEvent customEvent) {
                    var value = new LinkedHashMap<String, Object>();
                    value.put("originalName", customEvent.getName());
                    value.put("originalValue", customEvent.getValue());
                    value.put("convertedBy", "exampleAgentEventConverter");
                    context.emit(new AguiEvent.Custom(
                            context.getThreadId(),
                            context.getRunId(),
                            "example_custom_value",
                            value
                    ));
                }
            }

        };
    }

    @Bean
    public AguiEventEnricher aguiEventEnricher() {
        return (source, events, context) -> {
            final var timestamp = System.currentTimeMillis();
            return events.stream()
                    .map(event -> {
                        var newTimestamp = event.timestamp() != null ? event.timestamp() : timestamp;
                        var rawEvent = event.rawEvent();
                        if (rawEvent == null && source instanceof CustomEvent customEvent) {
                            var value = new LinkedHashMap<String, Object>();
                            value.put("agentEventType", customEvent.getType().name());
                            if (customEvent.getName() != null) {
                                value.put("name", customEvent.getName());
                            }
                            rawEvent = value;
                        }
                        return AguiEvents.withBaseProperties(event, newTimestamp, rawEvent);
                    })
                    .toList();
        };
    }

    @Bean
    public MiddlewareBase customEventMiddleware() {
        return new MiddlewareBase() {

            @Override
            public Flux<AgentEvent> onAgent(
                    Agent agent,
                    RuntimeContext ctx,
                    AgentInput input,
                    Function<AgentInput, Flux<AgentEvent>> next
            ) {
                var value = new HashMap<String, Object>();
                value.put("agentName", agent.getName());
                if (ctx != null && ctx.getSessionId() != null) {
                    value.put("sessionId", ctx.getSessionId());
                }
                var event = new CustomEvent("middleware_event", value);
                return next.apply(input)
                        .concatMap(agentEvent -> agentEvent instanceof AgentStartEvent
                                ? Flux.just(agentEvent, event) : Flux.just(agentEvent));
            }

        };
    }

    @Bean
    public Toolkit toolkit() {
        var toolkit = new Toolkit();
        toolkit.registration()
                .mcpClient(McpClientBuilder.create("amap")
                        .sseTransport("https://mcp.amap.com/sse?key=" + System.getenv("AMAP_API_KEY"))
                        .buildSync())
                .apply();
        return toolkit;
    }

    @Bean
    public Model model() {
        return OpenAIChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("qwen3.7-plus")
                .baseUrl("https://ws-1t9uu8m17ouv3le5.cn-beijing.maas.aliyuncs.com/compatible-mode/v1")
                .stream(true)
                .formatter(new OpenAIChatFormatter())
                .build();
    }

    @Bean
    public AguiAdapterConfig aguiAdapterConfig(
            List<AgentEventConverter> eventConverters,
            List<AguiEventEnricher> eventEnrichers
    ) {
        return AguiAdapterConfig.builder()
                .runTimeout(Duration.ofMinutes(10))
                .emitStateEvents(emitStateEvents)
                .emitToolCallArgs(emitToolCallArgs)
                .emitTokenUsage(emitTokenUsage)
                .enableReasoning(enableReasoning)
                .emitRunFinishedAfterError(emitRunFinishedAfterError)
                .defaultAgentId(defaultAgentId)
                .eventConverters(eventConverters)
                .eventEnrichers(eventEnrichers)
                .build();
    }

    @Bean
    public AguiRequestProcessor aguiRequestProcessor(AguiAgentRegistry registry, AguiAdapterConfig config) {
        var resolver = new AgentResolver() {

            @Override
            public Agent resolveAgent(String agentId, String threadId) {
                return registry.getAgent(agentId).orElseThrow(() -> new AguiException.AgentNotFoundException(agentId));
            }

            @Override
            public boolean hasMemory(RuntimeContext runtimeContext) {
                return false;
            }

        };
        return AguiRequestProcessor.builder()
                .agentResolver(resolver)
                .config(config)
                .build();
    }

    private Agent defaultAgent() {
        return ReActAgent.builder()
                .name("AG-UI Assistant")
                .sysPrompt("You are a helpful AI assistant exposed via the AG-UI protocol. "
                        + "You can help users with various tasks including weather queries "
                        + "and calculations. Be concise and helpful in your responses.")
                .model(model())
                .build();
    }

    private Agent chatAgent() {
        return ReActAgent.builder()
                .name("Chat Assistant")
                .sysPrompt("You are a friendly conversational assistant. "
                        + "Engage in natural conversation and help users "
                        + "with general questions and discussions.")
                .model(model())
                .toolkit(toolkit())
                .middleware(customEventMiddleware())
                .build();
    }

}
