package cn.ivfzhou.java.agentscope.reactagent;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEndEvent;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.event.AgentEventType;
import io.agentscope.core.event.AgentResultEvent;
import io.agentscope.core.event.AgentStartEvent;
import io.agentscope.core.event.ConfirmResult;
import io.agentscope.core.event.ModelCallEndEvent;
import io.agentscope.core.event.ModelCallStartEvent;
import io.agentscope.core.event.RequestStopEvent;
import io.agentscope.core.event.RequireUserConfirmEvent;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.event.TextBlockEndEvent;
import io.agentscope.core.event.TextBlockStartEvent;
import io.agentscope.core.event.ThinkingBlockDeltaEvent;
import io.agentscope.core.event.ThinkingBlockEndEvent;
import io.agentscope.core.event.ThinkingBlockStartEvent;
import io.agentscope.core.event.ToolCallDeltaEvent;
import io.agentscope.core.event.ToolCallEndEvent;
import io.agentscope.core.event.ToolCallStartEvent;
import io.agentscope.core.event.ToolResultEndEvent;
import io.agentscope.core.event.ToolResultStartEvent;
import io.agentscope.core.event.ToolResultTextDeltaEvent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.permission.PermissionMode;
import io.agentscope.core.state.JsonFileAgentStateStore;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.mcp.McpClientBuilder;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.extensions.model.dashscope.formatter.DashScopeChatFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sample {

    static void main() throws IOException, URISyntaxException {
        var toolkit = new Toolkit();
        toolkit.registerTool(new WriteFileTool());
        toolkit.registerMcpClient(McpClientBuilder.create("amap")
                .streamableHttpTransport("https://mcp.amap.com/mcp?key=" + System.getenv("AMAP_API_KEY"))
                .buildSync()
        ).block();

        var agent = ReActAgent.builder()
                .name("answer-helper")
                .sysPrompt("你是一个全知助手，解决回答各类问题。")
                // .model("dashscope:qwen-plus")
                .model(DashScopeChatModel.builder()
                        .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                        .modelName("qwen-plus")
                        .stream(true)
                        .formatter(new DashScopeChatFormatter())
                        .build()
                )
                .toolkit(toolkit)
                .permissionContext(PermissionContextState.builder()
                        .mode(PermissionMode.ACCEPT_EDITS)
                        .build()
                )
                .stateStore(new JsonFileAgentStateStore(getWorkspace()))
                .build();
        try (agent) {
            chat(agent, "ivfzhou", "session-1");
        }
    }

    private static Path getWorkspace() throws URISyntaxException {
        var path = Paths.get(Sample.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (!path.toFile().isDirectory()) {
            path = path.getParent();
        }
        System.out.println("workspace is " + path.toString());
        return path;
    }

    private static void chatOnce(ReActAgent agent, String userId, String sessionId, String ask) {
        var ctx = RuntimeContext.builder()
                .userId(userId)
                .sessionId(sessionId)
                .build();
        var msg = agent.call(ask, ctx).block();
        System.out.println("[ID] " + msg.getId());
        System.out.println("[Name] " + msg.getName());
        System.out.println("[RoleName] " + msg.getRole().name());
        System.out.println("[Timestamp] " + msg.getTimestamp());
        System.out.println("[GenerateReasonName] " + msg.getGenerateReason().name());
        var chatUsage = msg.getChatUsage();
        System.out.println("[ChatUsage]"
                + " time=" + chatUsage.getTime()
                + " inputTokens=" + chatUsage.getInputTokens()
                + " outputTokens=" + chatUsage.getOutputTokens()
                + " cachedTokens=" + chatUsage.getCachedTokens()
                + " totalTokens=" + chatUsage.getTotalTokens()
        );
        System.out.println("[Metadata] ");
        msg.getMetadata().forEach((key, value) -> System.out.println("    " + key + "=" + value));
        System.out.println("[Content] ");
        var list = msg.getContent();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + i + "] " + list.get(i));
        }
    }

    private static void chat(ReActAgent agent, String userId, String sessionId) throws IOException {
        var ctx = RuntimeContext.builder()
                .sessionId(sessionId)
                .userId(userId)
                .build();

        System.out.println("enter message to chat");
        var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        var msg = reader.readLine();
        try (reader) {
            List<ConfirmResult> confirmResults = new ArrayList<>();
            while (msg != null) {
                if (msg.equalsIgnoreCase("quit")) {
                    break;
                }

                if (msg.equalsIgnoreCase("interrupt")) {
                    // 中断该 session 正在进行的 call。
                    agent.interrupt(ctx);
                    continue;
                }

                if (msg.equalsIgnoreCase("interrupt with recovery message")) {
                    // 带消息中断——中断消息会被 LLM 在恢复时看到。
                    agent.interrupt(ctx, new UserMessage("用户已取消操作"));
                }

                UserMessage userMessage;
                if (msg.equalsIgnoreCase("confirm")) {
                    userMessage = UserMessage.builder()
                            .metadata(Map.of(Msg.METADATA_CONFIRM_RESULTS, confirmResults))
                            .build();
                } else {
                    userMessage = new UserMessage(msg);
                }

                agent.streamEvents(userMessage, ctx).doOnNext(event -> {
                    var crs = handleEvent(event);
                    if (crs != null && !crs.isEmpty()) {
                        confirmResults.addAll(crs);
                    }
                }).blockLast();
                msg = reader.readLine();
            }
        }
    }

    private static void printMsg(Msg msg) {
        System.out.println("[agent result begin]");
        System.out.println("id=" + msg.getId());
        System.out.println("name=" + msg.getName());
        System.out.println("timestamp=" + msg.getTimestamp());
        System.out.println("generateReason=" + msg.getGenerateReason());
        System.out.println("role=" + msg.getRole().name());
        System.out.println("[agent result end]");
    }

    private static List<ConfirmResult> handleEvent(AgentEvent event) {
        var confirmResults = new ArrayList<ConfirmResult>();
        switch (event.getType()) {
            case AgentEventType.AGENT_START:
                var agentStartEvent = (AgentStartEvent) event;
                System.out.println("[start agent]"
                        + " replyId=" + agentStartEvent.getReplyId()
                        + " name=" + agentStartEvent.getName()
                        + " role=" + agentStartEvent.getRole()
                        + " sessionId=" + agentStartEvent.getSessionId()
                );
                break;
            case AgentEventType.AGENT_RESULT:
                var agentResultEvent = (AgentResultEvent) event;
                var result = agentResultEvent.getResult();
                printMsg(result);
                break;
            case AgentEventType.AGENT_END:
                var agentEndEvent = (AgentEndEvent) event;
                System.out.println("[end agent]" + " replyId=" + agentEndEvent.getReplyId());
                break;
            case AgentEventType.MODEL_CALL_START:
                var modelCallStartEvent = (ModelCallStartEvent) event;
                System.out.println("[start model] " + "replyId=" + modelCallStartEvent.getReplyId());
                break;
            case AgentEventType.MODEL_CALL_END:
                var modelCallEndEvent = (ModelCallEndEvent) event;
                var usage = modelCallEndEvent.getUsage();
                System.out.println("[end model]"
                        + " replyId=" + modelCallEndEvent.getReplyId()
                        + " inputTokens=" + usage.getInputTokens()
                        + " outputTokens=" + usage.getOutputTokens()
                        + " cachedTokens=" + usage.getCachedTokens()
                        + " time=" + usage.getTime()
                        + " totalTokens=" + usage.getTotalTokens()
                );
                break;
            case AgentEventType.THINKING_BLOCK_START:
                var thinkingBlockStartEvent = (ThinkingBlockStartEvent) event;
                System.out.println("[start thinking]"
                        + " replyId=" + thinkingBlockStartEvent.getReplyId()
                        + " blockId=" + thinkingBlockStartEvent.getBlockId()
                );
                break;
            case AgentEventType.THINKING_BLOCK_DELTA:
                var thinkingBlockDeltaEvent = (ThinkingBlockDeltaEvent) event;
                System.out.print(thinkingBlockDeltaEvent.getDelta());
                break;
            case AgentEventType.THINKING_BLOCK_END:
                var thinkingBlockEndEvent = (ThinkingBlockEndEvent) event;
                System.out.println(System.lineSeparator() + "[end thinking]"
                        + " replyId=" + thinkingBlockEndEvent.getReplyId()
                        + " blockId=" + thinkingBlockEndEvent.getBlockId()
                );
                break;
            case AgentEventType.TEXT_BLOCK_START:
                var textBlockStartEvent = (TextBlockStartEvent) event;
                System.out.println("[start text]"
                        + " replyId=" + textBlockStartEvent.getReplyId()
                        + " blockId=" + textBlockStartEvent.getBlockId()
                );
                break;
            case AgentEventType.TEXT_BLOCK_DELTA:
                var textBlockDeltaEvent = (TextBlockDeltaEvent) event;
                System.out.print(textBlockDeltaEvent.getDelta());
                break;
            case AgentEventType.TEXT_BLOCK_END:
                var textBlockEndEvent = (TextBlockEndEvent) event;
                System.out.println(System.lineSeparator() + "[end text]"
                        + " replyId=" + textBlockEndEvent.getReplyId()
                        + " blockId=" + textBlockEndEvent.getBlockId()
                );
                break;
            case AgentEventType.TOOL_CALL_START:
                var toolCallStartEvent = (ToolCallStartEvent) event;
                System.out.println("[start tool] " + toolCallStartEvent.getToolCallName()
                        + " replyId=" + toolCallStartEvent.getReplyId()
                        + " callId=" + toolCallStartEvent.getToolCallId()
                );
                break;
            case AgentEventType.TOOL_CALL_DELTA:
                var toolCallDeltaEvent = (ToolCallDeltaEvent) event;
                System.out.print(toolCallDeltaEvent.getDelta());
                break;
            case AgentEventType.TOOL_CALL_END:
                var toolCallEndEvent = (ToolCallEndEvent) event;
                System.out.println(System.lineSeparator()
                        + "[end tool] " + toolCallEndEvent.getToolCallName()
                        + " replyId=" + toolCallEndEvent.getReplyId()
                        + " callId=" + toolCallEndEvent.getToolCallId()
                );
                break;
            case AgentEventType.TOOL_RESULT_START:
                var toolResultStartEvent = (ToolResultStartEvent) event;
                System.out.println("[start tool result] " + toolResultStartEvent.getToolCallName()
                        + " replyId=" + toolResultStartEvent.getReplyId()
                        + " callId=" + toolResultStartEvent.getToolCallId()
                );
                break;
            case AgentEventType.TOOL_RESULT_TEXT_DELTA:
                var toolResultTextDeltaEvent = (ToolResultTextDeltaEvent) event;
                System.out.print(toolResultTextDeltaEvent.getDelta());
                break;
            case AgentEventType.TOOL_RESULT_END:
                var toolResultEndEvent = (ToolResultEndEvent) event;
                var state = toolResultEndEvent.getState();
                System.out.println(System.lineSeparator()
                        + "[end tool result] " + toolResultEndEvent.getToolCallName()
                        + " replyId=" + toolResultEndEvent.getReplyId()
                        + " callId=" + toolResultEndEvent.getToolCallId()
                        + " stateValue=" + state.getValue()
                );
                break;
            case AgentEventType.REQUIRE_USER_CONFIRM:
                var requireUserConfirmEvent = (RequireUserConfirmEvent) event;
                System.out.println("[require user confirm]" + " replyId=" + requireUserConfirmEvent.getReplyId());
                var toolCalls = requireUserConfirmEvent.getToolCalls();
                for (int i = 0; i < toolCalls.size(); i++) {
                    var toolUseBlock = toolCalls.get(i);
                    System.out.print(i + "."
                            + " id=" + toolUseBlock.getId()
                            + " name=" + toolUseBlock.getName()
                            + " stateValue=" + toolUseBlock.getState().getValue()
                            + " content=" + toolUseBlock.getContent()
                    );
                    System.out.print(" metadata=");
                    var metadata = toolUseBlock.getMetadata();
                    metadata.forEach((k, v) -> System.out.print(k + "=" + v));
                    System.out.print(" input=");
                    var input = toolUseBlock.getInput();
                    input.forEach((k, v) -> System.out.print(k + "=" + v));
                    System.out.println();
                    confirmResults.add(new ConfirmResult(true, toolUseBlock));
                }
                break;
            case AgentEventType.REQUEST_STOP:
                var requestStopEvent = (RequestStopEvent) event;
                System.out.println("[request stop]"
                        + " getGenerateReason=" + requestStopEvent.getGenerateReason()
                        + " Reason=" + requestStopEvent.getReason()
                );

                break;
            default:
                System.out.println("skip event type is " + event.getType().getValue());
                break;
        }

        return confirmResults;
    }

}
