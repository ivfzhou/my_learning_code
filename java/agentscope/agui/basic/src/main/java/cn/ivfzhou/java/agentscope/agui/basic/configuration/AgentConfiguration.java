package cn.ivfzhou.java.agentscope.agui.basic.configuration;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Agent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.agui.AguiException;
import io.agentscope.core.agui.adapter.AguiAdapterConfig;
import io.agentscope.core.agui.adapter.strategy.AgentEventConverter;
import io.agentscope.core.agui.adapter.strategy.AguiEventEnricher;
import io.agentscope.core.agui.adapter.strategy.AguiStreamContext;
import io.agentscope.core.agui.encoder.AguiEventEncoder;
import io.agentscope.core.agui.event.AguiEvent;
import io.agentscope.core.agui.event.AguiEvents;
import io.agentscope.core.agui.model.RunAgentInput;
import io.agentscope.core.agui.processor.AgentResolver;
import io.agentscope.core.agui.processor.AguiRequestProcessor;
import io.agentscope.core.agui.registry.AguiAgentRegistry;
import io.agentscope.core.agui.runtime.AguiRequestBodyParser;
import io.agentscope.core.agui.runtime.AguiRuntimeContextRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Configuration
public class AgentConfiguration {

    private final String pathPrefix = "/agui";
    private final String defaultAgentId = "default";
    private final String agentIdHeader = "X-Agent-Id";
    private final boolean enablePathRouting = true;
    private final boolean emitStateEvents = true;
    private final boolean emitToolCallArgs = true;
    private final boolean emitTokenUsage = true;
    private final boolean enableReasoning = true;
    private final boolean emitRunFinishedAfterError = false;

    @Bean
    public AguiAgentRegistry aguiAgentRegistry() {
        var registry = new AguiAgentRegistry();
        registry.registerFactory("default", this::defaultAgent);
        registry.registerFactory("chat", this::chatAgent);
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
                var event = new CustomEvent("example_agent_event", value);
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

    @Bean
    public RouterFunction<ServerResponse> aguiRoutes(AguiRequestProcessor processor) {
        var encoder = new AguiEventEncoder();
        var parser = new AguiRequestBodyParser();

        HandlerFunction<ServerResponse> handle = request -> handle(parser, processor, encoder, request, null);

        HandlerFunction<ServerResponse> handleWithAgentId = request -> {
            var pathAgentId = request.pathVariable("agentId");
            return handle(parser, processor, encoder, request, pathAgentId);
        };

        var builder = RouterFunctions.route().POST(pathPrefix + "/run", handle);
        if (enablePathRouting) {
            builder.POST(pathPrefix + "/run/{agentId}", handleWithAgentId);
        }
        return builder.build();
    }

    private Mono<ServerResponse> handle(
            AguiRequestBodyParser parser,
            AguiRequestProcessor processor,
            AguiEventEncoder encoder,
            ServerRequest request,
            String pathAgentId
    ) {
        return request.bodyToMono(String.class)
                .map(parser::parse)
                .flatMap(input -> process(processor, encoder, request, input, pathAgentId))
                .onErrorResume(error -> parseError(encoder, error));
    }

    private Mono<ServerResponse> process(
            AguiRequestProcessor processor,
            AguiEventEncoder encoder,
            ServerRequest request,
            RunAgentInput input,
            String pathAgentId
    ) {
        var threadId = input.getThreadId();
        var runId = input.getRunId();
        try {
            var headerAgentId = request.headers().firstHeader(agentIdHeader);

            AguiRequestProcessor.ProcessResult result = processor.process(
                    AguiRuntimeContextRequest.<ServerRequest>builder()
                            .input(input)
                            .headerAgentId(headerAgentId)
                            .pathAgentId(pathAgentId)
                            .transport(AguiRuntimeContextRequest.Transport.WEBFLUX)
                            .method(request.method().name())
                            .path(request.path())
                            .nativeRequest(request)
                            .build());

            Flux<ServerSentEvent<String>> sse = result.events()
                    .map(event -> ServerSentEvent.<String>builder()
                            .data(encoder.encodeToJson(event).trim())
                            .build())
                    .doOnCancel(() -> result.interrupt(threadId));

            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(sse, ServerSentEvent.class);
        } catch (AguiException.AgentNotFoundException e) {
            return errorResponse(encoder, threadId, runId, e.getMessage());
        } catch (Exception e) {
            return errorResponse(encoder, threadId, runId, e.getMessage());
        }
    }

    private Mono<ServerResponse> parseError(AguiEventEncoder encoder, Throwable error) {
        return ServerResponse.badRequest()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(errorStream(encoder, "unknown", "unknown", "Failed to parse request: " + error.getMessage()), ServerSentEvent.class);
    }

    private Mono<ServerResponse> errorResponse(AguiEventEncoder encoder, String threadId, String runId, String message) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(errorStream(encoder, threadId, runId, message), ServerSentEvent.class);
    }

    private Flux<ServerSentEvent<String>> errorStream(AguiEventEncoder encoder, String threadId, String runId, String message) {
        var errorEvent = encoder
                .encodeToJson(new AguiEvent.Raw(threadId, runId, Map.of("error", message == null ? "Unknown error" : message)))
                .trim();
        var finishEvent = encoder.encodeToJson(new AguiEvent.RunFinished(threadId, runId)).trim();
        return Flux.just(
                ServerSentEvent.<String>builder().data(errorEvent).build(),
                ServerSentEvent.<String>builder().data(finishEvent).build()
        );
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
