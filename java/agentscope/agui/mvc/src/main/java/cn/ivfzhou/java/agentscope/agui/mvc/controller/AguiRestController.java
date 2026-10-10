package cn.ivfzhou.java.agentscope.agui.mvc.controller;

import io.agentscope.core.agui.AguiException;
import io.agentscope.core.agui.encoder.AguiEventEncoder;
import io.agentscope.core.agui.event.AguiEvent;
import io.agentscope.core.agui.model.RunAgentInput;
import io.agentscope.core.agui.processor.AguiRequestProcessor;
import io.agentscope.core.agui.runtime.AguiRequestBodyParser;
import io.agentscope.core.agui.runtime.AguiRuntimeContextRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.BaseSubscriber;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class AguiRestController {

    private static final Logger log = LoggerFactory.getLogger(AguiRestController.class);

    private final AguiRequestProcessor processor;
    private final AguiEventEncoder encoder = new AguiEventEncoder();
    private final AguiRequestBodyParser parser = new AguiRequestBodyParser();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public AguiRestController(AguiRequestProcessor processor) {
        this.processor = processor;
    }

    // 请求体 JSON 解析失败时返回 400 + SSE 兼容的 error/finish 事件。
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleParseError(Exception error) {
        var errorEvent = encoder.encodeToJson(new AguiEvent.Raw("unknown", "unknown",
                Map.of("error", "Failed to parse request: " + error.getMessage()))).trim();
        var finishEvent = encoder.encodeToJson(new AguiEvent.RunFinished("unknown", "unknown")).trim();
        return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body("data: " + errorEvent + "\n\ndata: " + finishEvent + "\n\n");
    }

    @PostMapping(path = "/agui/run/{agentId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter runWithAgentId(
            @PathVariable("agentId") String agentId,
            @RequestBody String body,
            @RequestHeader(name = "X-Agent-Id", required = false) String headerAgentId,
            HttpServletRequest request
    ) {
        return handle(parser.parse(body), headerAgentId, agentId, request);
    }

    @PostMapping(path = "/agui/run", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter run(
            @RequestBody String body,
            @RequestHeader(name = "X-Agent-Id", required = false) String headerAgentId,
            HttpServletRequest request
    ) {
        return handle(parser.parse(body), headerAgentId, null, request);
    }

    private SseEmitter handle(
            RunAgentInput input,
            String headerAgentId,
            String pathAgentId,
            HttpServletRequest request
    ) {
        // 0L = 不超时，由 Agent 跑完或客户端断开来收尾。
        var emitter = new SseEmitter(0L);
        var threadId = input.getThreadId();
        var runId = input.getRunId();

        executor.submit(() -> {
            try {
                var result = processor.process(AguiRuntimeContextRequest.<HttpServletRequest>builder()
                        .input(input)
                        .headerAgentId(headerAgentId)
                        .pathAgentId(pathAgentId)
                        .transport(AguiRuntimeContextRequest.Transport.MVC)
                        .method(request.getMethod())
                        .path(request.getRequestURI())
                        .nativeRequest(request)
                        .build());

                var subscription = new BaseSubscriber<AguiEvent>() {
                    @Override
                    protected void hookOnNext(AguiEvent event) {
                        send(emitter, encoder.encodeToJson(event));
                    }

                    @Override
                    protected void hookOnError(Throwable error) {
                        log.error("AG-UI run error: {}", error.getMessage());
                        sendErrorAndComplete(emitter, threadId, runId, error.getMessage());
                    }

                    @Override
                    protected void hookOnComplete() {
                        emitter.complete();
                    }
                };

                // 客户端断开、超时、出错时中断 Agent，等价于 WebFlux 的 doOnCancel
                emitter.onCompletion(() -> result.interrupt(threadId));
                emitter.onTimeout(() -> result.interrupt(threadId));
                emitter.onError(e -> result.interrupt(threadId));

                result.events().subscribe(subscription);
            } catch (AguiException.AgentNotFoundException e) {
                sendErrorAndComplete(emitter, threadId, runId, e.getMessage());
            } catch (Exception e) {
                sendErrorAndComplete(emitter, threadId, runId, e.getMessage());
            }
        });

        return emitter;
    }

    private void send(SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event().data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private void sendErrorAndComplete(SseEmitter emitter, String threadId, String runId, String message) {
        send(emitter, encoder.encodeToJson(new AguiEvent.Raw(threadId, runId,
                Map.of("error", message == null ? "Unknown error" : message))));
        send(emitter, encoder.encodeToJson(new AguiEvent.RunFinished(threadId, runId)));
        emitter.complete();
    }
}
