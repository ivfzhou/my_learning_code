package cn.ivfzhou.java.agentscope.harnessagent;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEndEvent;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.event.AgentEventType;
import io.agentscope.core.event.AgentResultEvent;
import io.agentscope.core.event.AgentStartEvent;
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
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Sample {

    static void main() throws URISyntaxException, IOException {
        var agent = HarnessAgent.builder()
                .name("note-taker")
                .sysPrompt("你是一个帮助用户做笔记的助手。")
                .model(DashScopeChatModel.builder()
                        .enableSearch(true)
                        .enableThinking(true)
                        .enableEncrypt(true)
                        .modelName("qwen-plus")
                        .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                        .build())
                .workspace(getWorkspace())
                .compaction(CompactionConfig.builder()
                        .triggerMessages(30)
                        .keepMessages(10)
                        .build())
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

    private static void chat(HarnessAgent agent, String userId, String sessionId) throws IOException {
        var ctx = RuntimeContext.builder()
                .sessionId(sessionId)
                .userId(userId)
                .build();

        System.out.println("enter message to chat");
        var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        var msg = reader.readLine();
        try (reader) {
            while (msg != null) {
                if (msg.equalsIgnoreCase("quit")) {
                    break;
                }

                agent.streamEvents(new UserMessage(msg), ctx).doOnNext(Sample::handleEvent).blockLast();
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

    private static void handleEvent(AgentEvent event) {
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
    }

}
