package cn.ivfzhou.java.agentscope.springboot;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEndEvent;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.event.AgentEventType;
import io.agentscope.core.event.AgentResultEvent;
import io.agentscope.core.event.AgentStartEvent;
import io.agentscope.core.event.ConfirmResult;
import io.agentscope.core.event.DataBlockDeltaEvent;
import io.agentscope.core.event.DataBlockEndEvent;
import io.agentscope.core.event.DataBlockStartEvent;
import io.agentscope.core.event.ExternalExecutionResultEvent;
import io.agentscope.core.event.ModelCallEndEvent;
import io.agentscope.core.event.ModelCallStartEvent;
import io.agentscope.core.event.RequestStopEvent;
import io.agentscope.core.event.RequireExternalExecutionEvent;
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
import io.agentscope.core.event.UserConfirmResultEvent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.message.ToolCallState;
import io.agentscope.core.message.ToolResultBlock;
import io.agentscope.core.message.ToolResultMessage;
import io.agentscope.core.message.ToolResultState;
import io.agentscope.core.message.ToolUseBlock;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.permission.PermissionBehavior;
import io.agentscope.core.permission.PermissionRule;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Sample implements ApplicationRunner {

    static void main() {
        SpringApplication.run(Sample.class);
    }

    @Resource
    private ReActAgent agent;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            chat(agent, "ivfzhou", "seesion-1");
        } finally {
            agent.close();
        }
    }

    private static void chat(ReActAgent agent, String userId, String sessionId) throws IOException {
        var ctx = RuntimeContext.builder()
                .sessionId(sessionId)
                .userId(userId)
                .build();

        System.out.println("enter message to chat:");
        var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        var ask = reader.readLine();
        try (reader) {
            final List<ToolUseBlock> toolUseBlocks = new ArrayList<>();
            while (ask != null) {

                if (ask.equalsIgnoreCase("quit")) {
                    break;
                }

                if (ask.equalsIgnoreCase("interrupt")) {
                    agent.interrupt(userId, sessionId);
                    continue;
                }

                if (ask.equalsIgnoreCase("interrupt with recovery message")) {
                    agent.interrupt(userId, sessionId, Msg.builder().textContent(ask).build());
                }

                Msg msg;
                if (ask.equalsIgnoreCase("confirm") && !toolUseBlocks.isEmpty()) {
                    var confirmResults = new ArrayList<ConfirmResult>();
                    for (int i = 0; i < toolUseBlocks.size(); i++) {
                        var toolUseBlock = toolUseBlocks.get(i);
                        System.out.print("enter tool " + toolUseBlock.getName() + " result [" + (i + 1) + "]: (y/n)");
                        var ret = reader.readLine();
                        if (ret.equalsIgnoreCase("y")) {
                            confirmResults.add(new ConfirmResult(true, toolUseBlock, List.of(new PermissionRule(toolUseBlock.getName(), null, PermissionBehavior.ALLOW, "userSettings"))));
                        } else {
                            confirmResults.add(new ConfirmResult(false, toolUseBlock));
                        }
                    }
                    msg = UserMessage.builder().metadata(Map.of(Msg.METADATA_CONFIRM_RESULTS, confirmResults)).build();
                    toolUseBlocks.clear();
                } else if (ask.equalsIgnoreCase("execute") && !toolUseBlocks.isEmpty()) {
                    var toolResultBlocks = new ArrayList<ToolResultBlock>();
                    for (int i = 0; i < toolUseBlocks.size(); i++) {
                        var toolUseBlock = toolUseBlocks.get(i);
                        System.out.print("enter external tool " + toolUseBlock.getName() + " result [" + (i + 1) + "]:");
                        toolResultBlocks.add(ToolResultBlock.builder()
                                .id(toolUseBlock.getId())
                                .name(toolUseBlock.getName())
                                .state(ToolResultState.SUCCESS)
                                .output(List.of(TextBlock.builder().text(reader.readLine()).build()))
                                .build());
                    }
                    msg = ToolResultMessage.builder().results(toolResultBlocks).build();
                    toolUseBlocks.clear();
                } else {
                    msg = new UserMessage(ask);
                }

                agent.streamEvents(msg, ctx).doOnNext(event -> {
                    var list = handleEvent(event);
                    if (list != null && !list.isEmpty()) toolUseBlocks.addAll(list);
                }).blockLast();

                ask = reader.readLine();
            }
        }
    }

    private static List<ToolUseBlock> handleEvent(AgentEvent event) {
        switch (event.getType()) {
            case AgentEventType.AGENT_START:
                handleAgentStartEvent((AgentStartEvent) event);
                break;
            case AgentEventType.MODEL_CALL_START:
                handleModelCallStartEvent((ModelCallStartEvent) event);
                break;
            case AgentEventType.THINKING_BLOCK_START:
                handleThinkingBlockStartEvent((ThinkingBlockStartEvent) event);
                break;
            case AgentEventType.THINKING_BLOCK_DELTA:
                handleThinkingBlockDeltaEvent((ThinkingBlockDeltaEvent) event);
                break;
            case AgentEventType.THINKING_BLOCK_END:
                handleThinkingBlockEndEvent((ThinkingBlockEndEvent) event);
                break;
            case AgentEventType.TEXT_BLOCK_START:
                handleTextBlockStartEvent((TextBlockStartEvent) event);
                break;
            case AgentEventType.TEXT_BLOCK_DELTA:
                handleTextBlockDeltaEvent((TextBlockDeltaEvent) event);
                break;
            case AgentEventType.TEXT_BLOCK_END:
                handleTextBlockEndEvent((TextBlockEndEvent) event);
                break;
            case AgentEventType.DATA_BLOCK_START:
                handleDataBlockStartEvent((DataBlockStartEvent) event);
                break;
            case AgentEventType.DATA_BLOCK_DELTA:
                handleDataBlockDeltaEvent((DataBlockDeltaEvent) event);
                break;
            case AgentEventType.DATA_BLOCK_END:
                handleDataBlockEndEvent((DataBlockEndEvent) event);
                break;
            case AgentEventType.MODEL_CALL_END:
                handleModelCallEndEvent((ModelCallEndEvent) event);
                break;
            case AgentEventType.TOOL_CALL_START:
                handleToolCallStartEvent((ToolCallStartEvent) event);
                break;
            case AgentEventType.TOOL_CALL_DELTA:
                handleToolCallDeltaEvent((ToolCallDeltaEvent) event);
                break;
            case AgentEventType.TOOL_CALL_END:
                handleToolCallEndEvent((ToolCallEndEvent) event);
                break;
            case AgentEventType.REQUIRE_USER_CONFIRM:
                return handleRequireUserConfirmEvent((RequireUserConfirmEvent) event);
            case AgentEventType.USER_CONFIRM_RESULT:
                handleUserConfirmResultEvent((UserConfirmResultEvent) event);
                break;
            case AgentEventType.REQUIRE_EXTERNAL_EXECUTION:
                return handleRequireExternalExecutionEvent((RequireExternalExecutionEvent) event);
            case AgentEventType.EXTERNAL_EXECUTION_RESULT:
                handleExternalExecutionResultEvent((ExternalExecutionResultEvent) event);
                break;
            case AgentEventType.TOOL_RESULT_START:
                handleToolResultStartEvent((ToolResultStartEvent) event);
                break;
            case AgentEventType.TOOL_RESULT_TEXT_DELTA:
                handleToolResultTextDeltaEvent((ToolResultTextDeltaEvent) event);
                break;
            case AgentEventType.TOOL_RESULT_END:
                return handleToolResultEndEvent((ToolResultEndEvent) event);
            case AgentEventType.REQUEST_STOP:
                handleRequestStopEvent((RequestStopEvent) event);
                break;
            case AgentEventType.AGENT_RESULT:
                handleAgentResultEvent((AgentResultEvent) event);
                break;
            case AgentEventType.AGENT_END:
                handleAgentEndEvent((AgentEndEvent) event);
                break;
            default:
                System.out.println("!!! SKIP EVENT TYPE IS " + event.getType().getValue());
                break;
        }

        return null;
    }

    private static void handleAgentStartEvent(AgentStartEvent event) {
        System.out.println("[AGENT_START] " + event.getName());
    }

    private static void handleModelCallStartEvent(ModelCallStartEvent event) {
        System.out.println("[MODEL_CALL_START]");
    }

    private static void handleThinkingBlockStartEvent(ThinkingBlockStartEvent event) {
        System.out.println("[THINKING_BLOCK_START]");
    }

    private static void handleThinkingBlockDeltaEvent(ThinkingBlockDeltaEvent event) {
        System.out.print(event.getDelta());
    }

    private static void handleThinkingBlockEndEvent(ThinkingBlockEndEvent event) {
        System.out.println(System.lineSeparator() + "[THINKING_BLOCK_END]");
    }

    private static void handleTextBlockStartEvent(TextBlockStartEvent event) {
        System.out.println("[TEXT_BLOCK_START]");
    }

    private static void handleTextBlockDeltaEvent(TextBlockDeltaEvent event) {
        System.out.print(event.getDelta());
    }

    private static void handleTextBlockEndEvent(TextBlockEndEvent event) {
        System.out.println(System.lineSeparator() + "[TEXT_BLOCK_END]");
    }

    private static void handleDataBlockStartEvent(DataBlockStartEvent event) {
        System.out.println("[DATA_BLOCK_START]");
    }

    private static void handleDataBlockDeltaEvent(DataBlockDeltaEvent event) {
        System.out.print(event.getDelta());
    }

    private static void handleDataBlockEndEvent(DataBlockEndEvent event) {
        System.out.println(System.lineSeparator() + "[DATA_BLOCK_END]");
    }

    private static void handleModelCallEndEvent(ModelCallEndEvent event) {
        System.out.println("[MODEL_CALL_END]");
    }

    private static void handleToolCallStartEvent(ToolCallStartEvent event) {
        System.out.println("[TOOL_CALL_START] " + event.getToolCallName());
    }

    private static void handleToolCallDeltaEvent(ToolCallDeltaEvent event) {
        System.out.print(event.getDelta());
    }

    private static void handleToolCallEndEvent(ToolCallEndEvent event) {
        System.out.println(System.lineSeparator() + "[TOOL_CALL_END]");
    }

    private static List<ToolUseBlock> handleRequireUserConfirmEvent(RequireUserConfirmEvent event) {
        System.out.println("[REQUIRE_USER_CONFIRM]");
        return event.getToolCalls().stream()
                .filter(v -> v.getState() == ToolCallState.ASKING || v.getState() == ToolCallState.PENDING)
                .toList();
    }

    private static void handleUserConfirmResultEvent(UserConfirmResultEvent event) {
        System.out.println("[USER_CONFIRM_RESULT]");
    }

    private static List<ToolUseBlock> handleRequireExternalExecutionEvent(RequireExternalExecutionEvent event) {
        System.out.print("[REQUIRE_EXTERNAL_EXECUTION]");
        return event.getToolCalls();
    }

    private static void handleExternalExecutionResultEvent(ExternalExecutionResultEvent event) {
        System.out.println("[USER_CONFIRM_RESULT]");
    }

    private static void handleToolResultStartEvent(ToolResultStartEvent event) {
        System.out.println("[TOOL_RESULT_START]");
    }

    private static void handleToolResultTextDeltaEvent(ToolResultTextDeltaEvent event) {
        System.out.print(event.getDelta());
    }

    private static List<ToolUseBlock> handleToolResultEndEvent(ToolResultEndEvent event) {
        var state = event.getState();
        System.out.println(System.lineSeparator() + "[TOOL_RESULT_END]");

        // 外部工具被挂起（state=running）：记录待处理调用，供下次 execute 回填真实结果。
        if (state == ToolResultState.RUNNING) {
            System.out.println("[NEED_EXTERNAL_EXECUTE]");
            return List.of(
                    ToolUseBlock.builder()
                            .id(event.getToolCallId())
                            .name(event.getToolCallName())
                            .input(Map.of())
                            .build()
            );
        }

        return null;
    }

    private static void handleRequestStopEvent(RequestStopEvent event) {
        System.out.println("[REQUEST_STOP]");
    }

    private static void handleAgentResultEvent(AgentResultEvent event) {
        System.out.println("[AGENT_RESULT] " + event.getResult().getGenerateReason().name());
    }

    private static void handleAgentEndEvent(AgentEndEvent event) {
        System.out.println("[AGENT_END]");
    }
}
