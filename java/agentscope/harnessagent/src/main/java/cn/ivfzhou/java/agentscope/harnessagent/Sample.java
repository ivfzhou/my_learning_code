package cn.ivfzhou.java.agentscope.harnessagent;

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
import io.agentscope.core.permission.AdditionalWorkingDirectory;
import io.agentscope.core.permission.PermissionBehavior;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.permission.PermissionMode;
import io.agentscope.core.permission.PermissionRule;
import io.agentscope.core.skill.repository.FileSystemSkillRepository;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.builtin.TodoTools;
import io.agentscope.core.tool.mcp.McpClientBuilder;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.extensions.model.dashscope.formatter.DashScopeChatFormatter;
import io.agentscope.extensions.redis.RedisDistributedStore;
import io.agentscope.extensions.redis.state.RedisAgentStateStore;
import io.agentscope.harness.agent.HarnessAgent;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.RedisClient;

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

public final class Sample {

    static void main() throws IOException, URISyntaxException {
        var toolkit = new Toolkit();
        toolkit.registerMcpClient(
                McpClientBuilder.create("amap")
                        .streamableHttpTransport("https://mcp.amap.com/mcp?key=" + System.getenv("AMAP_API_KEY"))
                        .buildSync()
        ).block();
        toolkit.registerTool(new TodoTools());

        var model = DashScopeChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("qwen3-max")
                .stream(true)
                .formatter(new DashScopeChatFormatter()) // 负责把 AgentScope 的 Msg 对象转换为各提供商 API 期望的请求载荷。
                .nativeStructuredOutput(true) // 需要 LLM 支持结构化输出。
                .nativeStructuredOutputWithTools(false) // 不要优先遵循 response_format 约束而跳过工具调用。
                .build();

        var workspace = getWorkspace();
        var config = DefaultJedisClientConfig.builder().user("ivfzhou").password("123456").database(0).build();
        var redisClient = RedisClient.builder().hostAndPort("127.0.0.1", 6379).clientConfig(config).build();
        var agent = HarnessAgent.builder()
                .name("answer-helper")
                .sysPrompt("你是一个全知助手，能回答各类问题。")
                .model(model)
                .toolkit(toolkit)
                .permissionContext(
                        PermissionContextState.builder()
                                .mode(PermissionMode.DEFAULT)
                                .addWorkingDirectory(workspace.toAbsolutePath().toString(), new AdditionalWorkingDirectory(workspace.toAbsolutePath().toString(), "userSettings"))
                                .build()
                )
                .stateStore(new RedisAgentStateStore.Builder().jedisClient(redisClient).build())
                .distributedStore(RedisDistributedStore.fromJedis(redisClient))
                .enableTaskList(true)
                .skillRepository(new FileSystemSkillRepository(Path.of(System.getProperty("user.home"), ".agentscope", "skills"), true))
                .build();
        try (agent) {
            // chat(agent, "ivfzhou", "session-1");
            clearState(agent, "ivfzhou", "session-1");
        }
    }

    private static void clearState(HarnessAgent agent, String userId, String sessionId) {
        agent.clearContext(userId, sessionId);
    }

    private static void getState(HarnessAgent agent, String userId, String sessionId) {
        var agentState = agent.getDelegate().getAgentState(userId, sessionId);
        // AgentState.fromJsonString("{}");
        System.out.println(agentState.toJson());
    }

    private static void chat(HarnessAgent agent, String userId, String sessionId) throws IOException {
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
                    // 中断该 session 正在进行的 call。
                    agent.getDelegate().interrupt(userId, sessionId);
                    continue;
                }

                if (ask.equalsIgnoreCase("interrupt with recovery message")) {
                    // 带消息中断——中断消息会被 LLM 在恢复时看到。
                    agent.getDelegate().interrupt(userId, sessionId, Msg.builder().textContent(ask).build());
                }

                Msg msg;
                if (ask.equalsIgnoreCase("confirm") && !toolUseBlocks.isEmpty()) {
                    var confirmResults = new ArrayList<ConfirmResult>();
                    for (int i = 0; i < toolUseBlocks.size(); i++) {
                        var toolUseBlock = toolUseBlocks.get(i);
                        System.out.print("enter tool " + toolUseBlock.getName() + " result [" + (i + 1) + "]: (y/n)");
                        var ret = reader.readLine();
                        if (ret.equalsIgnoreCase("y")) {
                            confirmResults.add(new ConfirmResult(true, toolUseBlock,
                                    List.of(new PermissionRule(toolUseBlock.getName(), null, PermissionBehavior.ALLOW, "userSettings"))));
                        } else {
                            confirmResults.add(new ConfirmResult(false, toolUseBlock));
                        }
                    }
                    msg = UserMessage.builder()
                            .metadata(Map.of(Msg.METADATA_CONFIRM_RESULTS, confirmResults))
                            .build();
                    toolUseBlocks.clear();
                } else if (ask.equalsIgnoreCase("execute") && !toolUseBlocks.isEmpty()) {
                    var toolResultBlocks = new ArrayList<ToolResultBlock>();
                    for (int i = 0; i < toolUseBlocks.size(); i++) {
                        var toolUseBlock = toolUseBlocks.get(i);
                        System.out.print("enter external tool " + toolUseBlock.getName() + " result [" + (i + 1) + "]:");
                        toolResultBlocks.add(
                                ToolResultBlock.builder()
                                        .id(toolUseBlock.getId())
                                        .name(toolUseBlock.getName())
                                        .state(ToolResultState.SUCCESS)
                                        .output(List.of(TextBlock.builder().text(reader.readLine()).build()))
                                        .build()
                        );
                    }
                    msg = ToolResultMessage.builder()
                            .results(toolResultBlocks)
                            .build();
                    toolUseBlocks.clear();
                } else {
                    msg = new UserMessage(ask);
                }

                agent.streamEvents(msg, ctx).doOnNext(event -> {
                    var list = handleEvent(event);
                    if (list != null && !list.isEmpty()) toolUseBlocks.addAll(list);
                }).blockLast();

                System.out.println("[ToolUseBlocks] size=" + toolUseBlocks.size());
                ask = reader.readLine();
            }
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

    private static List<ToolUseBlock> handleEvent(AgentEvent event) {
        switch (event.getType()) {
            case AgentEventType.AGENT_START:
                var agentStartEvent = (AgentStartEvent) event;
                System.out.println("[AGENT_START]"
                        + " replyId=" + agentStartEvent.getReplyId()
                        + " name=" + agentStartEvent.getName()
                        + " role=" + agentStartEvent.getRole()
                        + " sessionId=" + agentStartEvent.getSessionId()
                );
                break;
            case AgentEventType.AGENT_END:
                var agentEndEvent = (AgentEndEvent) event;
                System.out.println("[AGENT_END]"
                        + " replyId=" + agentEndEvent.getReplyId()
                );
                break;
            case AgentEventType.AGENT_RESULT:
                var agentResultEvent = (AgentResultEvent) event;
                var result = agentResultEvent.getResult();
                System.out.println("[AGENT_RESULT]"
                        + " resultId=" + result.getId()
                        + " resultName=" + result.getName()
                        + " resultTimestamp=" + result.getTimestamp()
                        + " resultGenerateReason=" + result.getGenerateReason()
                        + " resultRole=" + result.getRole().name()
                );
                break;
            case AgentEventType.MODEL_CALL_START:
                var modelCallStartEvent = (ModelCallStartEvent) event;
                System.out.println("[MODEL_CALL_START] "
                        + "replyId=" + modelCallStartEvent.getReplyId()
                );
                break;
            case AgentEventType.MODEL_CALL_END:
                var modelCallEndEvent = (ModelCallEndEvent) event;
                var usage = modelCallEndEvent.getUsage();
                System.out.println("[MODEL_CALL_END]"
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
                System.out.println("[THINKING_BLOCK_START]"
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
                System.out.println(System.lineSeparator() + "[THINKING_BLOCK_END]"
                        + " replyId=" + thinkingBlockEndEvent.getReplyId()
                        + " blockId=" + thinkingBlockEndEvent.getBlockId()
                );
                break;
            case AgentEventType.TEXT_BLOCK_START:
                var textBlockStartEvent = (TextBlockStartEvent) event;
                System.out.println("[TEXT_BLOCK_START]"
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
                System.out.println(System.lineSeparator() + "[TEXT_BLOCK_END]"
                        + " replyId=" + textBlockEndEvent.getReplyId()
                        + " blockId=" + textBlockEndEvent.getBlockId()
                );
                break;
            case AgentEventType.TOOL_CALL_START:
                var toolCallStartEvent = (ToolCallStartEvent) event;
                System.out.println("[TOOL_CALL_START]"
                        + " toolCallName=" + toolCallStartEvent.getToolCallName()
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
                System.out.println(System.lineSeparator() + "[TOOL_CALL_END]"
                        + " toolCallName=" + toolCallEndEvent.getToolCallName()
                        + " replyId=" + toolCallEndEvent.getReplyId()
                        + " callId=" + toolCallEndEvent.getToolCallId()
                );
                break;
            case AgentEventType.TOOL_RESULT_START:
                var toolResultStartEvent = (ToolResultStartEvent) event;
                System.out.println("[TOOL_RESULT_START]"
                        + " toolCallName=" + toolResultStartEvent.getToolCallName()
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
                        + "[TOOL_RESULT_END]"
                        + " toolCallName=" + toolResultEndEvent.getToolCallName()
                        + " replyId=" + toolResultEndEvent.getReplyId()
                        + " callId=" + toolResultEndEvent.getToolCallId()
                        + " stateValue=" + state.getValue()
                );

                // 外部工具被挂起（state=running）：记录待处理调用，供下次 execute 回填真实结果。
                if (state == ToolResultState.RUNNING) {
                    System.out.println("[NEED_EXECUTE]");
                    return List.of(
                            ToolUseBlock.builder()
                                    .id(toolResultEndEvent.getToolCallId())
                                    .name(toolResultEndEvent.getToolCallName())
                                    .input(Map.of())
                                    .build()
                    );
                }

                break;
            case AgentEventType.REQUIRE_USER_CONFIRM:
                var requireUserConfirmEvent = (RequireUserConfirmEvent) event;
                System.out.println("[REQUIRE_USER_CONFIRM]" + " replyId=" + requireUserConfirmEvent.getReplyId());
                var toolCalls = requireUserConfirmEvent.getToolCalls();
                for (int i = 0; i < toolCalls.size(); i++) {
                    var toolUseBlock = toolCalls.get(i);
                    System.out.print("    " + (i + 1) + "."
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
                if (!toolCalls.isEmpty()) System.out.println("[NEED_CONFIRM]");
                return toolCalls.stream().filter(v -> v.getState() == ToolCallState.ASKING || v.getState() == ToolCallState.PENDING).toList();
            case AgentEventType.USER_CONFIRM_RESULT:
                var userConfirmResultEvent = (UserConfirmResultEvent) event;
                System.out.println("[USER_CONFIRM_RESULT] "
                        + " replyId=" + userConfirmResultEvent.getReplyId()
                        + " confirmResults=" + userConfirmResultEvent.getConfirmResults()
                );
                break;
            case AgentEventType.REQUEST_STOP:
                var requestStopEvent = (RequestStopEvent) event;
                System.out.println("[REQUEST_STOP]"
                        + " getGenerateReason=" + requestStopEvent.getGenerateReason()
                        + " Reason=" + requestStopEvent.getReason()
                );

                break;
            case AgentEventType.REQUIRE_EXTERNAL_EXECUTION:
                var requireExternalExecutionEvent = (RequireExternalExecutionEvent) event;
                var toolCalls2 = requireExternalExecutionEvent.getToolCalls();
                System.out.print("[REQUIRE_EXTERNAL_EXECUTION]"
                        + " replyId=" + requireExternalExecutionEvent.getReplyId()
                );
                for (int i = 0; i < toolCalls2.size(); i++) {
                    System.out.println("    " + (i + 1) + "."
                            + " name=" + toolCalls2.get(i).getName()
                            + " id=" + toolCalls2.get(i).getId()
                            + " stateValue=" + toolCalls2.get(i).getState().getValue()
                    );
                }
                if (!toolCalls2.isEmpty()) System.out.println("[NEED_EXECUTE]");
                return toolCalls2;
            default:
                System.out.println("!!! SKIP EVENT TYPE IS " + event.getType().getValue());
                break;
        }

        return null;
    }

}
