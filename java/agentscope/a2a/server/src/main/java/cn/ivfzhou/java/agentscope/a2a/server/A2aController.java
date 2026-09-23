package cn.ivfzhou.java.agentscope.a2a.server;

import io.a2a.spec.AgentCard;
import io.a2a.spec.InternalError;
import io.a2a.spec.JSONRPCErrorResponse;
import io.a2a.spec.TransportProtocol;
import io.a2a.util.Utils;
import io.agentscope.core.a2a.server.AgentScopeA2aServer;
import io.agentscope.core.a2a.server.transport.jsonrpc.JsonRpcTransportWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class A2aController {

    private static final Logger log = LoggerFactory.getLogger(A2aController.class);

    private final AgentScopeA2aServer server;

    private final JsonRpcTransportWrapper jsonRpcWrapper;

    public A2aController(AgentScopeA2aServer server) {
        this.server = server;
        this.jsonRpcWrapper = server.getTransportWrapper(TransportProtocol.JSONRPC.asString(), JsonRpcTransportWrapper.class);
    }

    @GetMapping(
            path = {"/.well-known/agent-card.json", "/.well-known/agent.json"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AgentCard agentCard() {
        return server.getAgentCard();
    }

    @PostMapping(path = A2aConfiguration.JSON_RPC_PATH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleStreaming(
            @RequestBody String body,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request
    ) {
        // 0L = 不超时，由 Agent 跑完或客户端断开来收尾（否则会落到容器的 async 默认超时）。
        var emitter = new SseEmitter(0L);
        var subscription = new AtomicReference<Disposable>();
        emitter.onCompletion(() -> cancel(subscription));
        emitter.onError(e -> cancel(subscription));
        emitter.onTimeout(() -> cancel(subscription));

        Object result;
        try {
            var metadata = new HashMap<String, Object>();
            metadata.put("remoteAddress", request.getRemoteAddr());

            result = jsonRpcWrapper.handleRequest(body, headers, metadata);
        } catch (Exception e) {
            log.error("handle a2a streaming request error", e);
            send(emitter, new JSONRPCErrorResponse(null, new InternalError(e.getMessage())));
            emitter.complete();
            return emitter;
        }

        if (!(result instanceof Flux<?> flux)) {
            // 客户端声明了 SSE，但方法其实不是流式方法：推一条事件就结束。
            send(emitter, result);
            emitter.complete();
            return emitter;
        }

        subscription.set(flux.subscribe(
                item -> send(emitter, item),
                error -> {
                    log.error("stream a2a response error", error);
                    send(emitter, new JSONRPCErrorResponse(null, new InternalError(error.getMessage())));
                    emitter.complete();
                },
                emitter::complete
        ));

        return emitter;
    }

    @PostMapping(path = A2aConfiguration.JSON_RPC_PATH, produces = MediaType.ALL_VALUE)
    public ResponseEntity<String> handleJsonRpc(
            @RequestBody String body,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request
    ) {
        try {
            var metadata = new HashMap<String, Object>();
            metadata.put("remoteAddress", request.getRemoteAddr());

            var result = jsonRpcWrapper.handleRequest(body, headers, metadata);
            if (result instanceof Flux<?>) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "streaming method requires Accept: " + MediaType.TEXT_EVENT_STREAM_VALUE);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Utils.toJsonString(result));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("handle a2a request error", e);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Utils.toJsonString(new JSONRPCErrorResponse(null, new InternalError(e.getMessage()))));
        }
    }

    private static void send(SseEmitter emitter, Object payload) {
        try {
            emitter.send(SseEmitter.event().data(Utils.toJsonString(payload)));
        } catch (IOException | IllegalStateException e) {
            // 客户端已断开或响应已结束。
            emitter.completeWithError(e);
        }
    }

    private static void cancel(AtomicReference<Disposable> subscription) {
        var disposable = subscription.get();
        if (null != disposable && !disposable.isDisposed()) {
            // 客户端断开时取消订阅，避免 Agent 继续空跑。
            disposable.dispose();
        }
    }

}
