package cn.ivfzhou.java.agentscope.harnessagent;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import io.agentscope.core.model.Model;
import io.agentscope.core.permission.PermissionBehavior;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.permission.PermissionMode;
import io.agentscope.core.permission.PermissionRule;
import io.agentscope.core.skill.repository.mysql.MysqlSkillRepository;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.builtin.TodoTools;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.extensions.model.dashscope.formatter.DashScopeChatFormatter;
import io.agentscope.extensions.mysql.store.JdbcStore;
import io.agentscope.extensions.mysql.store.MysqlJdbcStoreDialect;
import io.agentscope.extensions.redis.RedisDistributedStore;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.IsolationScope;
import io.agentscope.harness.agent.filesystem.spec.RemoteFilesystemSpec;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;
import io.agentscope.harness.agent.memory.compaction.ToolResultEvictionConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.UnifiedJedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Sample {

    static void main() throws IOException {
        var model = getModel();
        var workspace = getWorkspace();
        var dataSource = getDataSource();
        var redisClient = getRedisClient();
        var toolkit = getToolkit();

        var agent = HarnessAgent.builder()
                .name("aftersale-orchestrator")
                .sysPrompt("你是电商售后工单调度员。先按 workspace/AGENTS.md 的流程给工单定性，再用 agent_spawn 派发 policy-expert / order-analyst / risk-reviewer 并行核查，最后交给 reply-writer 成文并自己汇总。")
                .model(model)
                .toolkit(toolkit)
                .permissionContext(
                        PermissionContextState.builder()
                                .mode(PermissionMode.DEFAULT)
                                .build()
                )
                // .stateStore(new JsonFileAgentStateStore(Paths.get(workspace.toAbsolutePath().toString(), "states")))
                // .stateStore(new MysqlAgentStateStore(dataSource, "agentscope", "agentscope-states", true))
                .distributedStore(RedisDistributedStore.fromJedis(redisClient))
                .compaction(
                        CompactionConfig.builder()
                                .triggerMessages(30)
                                .keepMessages(10)
                                .truncateArgs(
                                        CompactionConfig.TruncateArgsConfig.builder()
                                                .maxArgLength(2000)
                                                .truncationText("... [truncated] ...")
                                                .build()
                                )
                                .build()
                )
                .toolResultEviction(ToolResultEvictionConfig.defaults())
                .workspace(workspace)
                // .filesystem(new LocalFilesystemSpec().isolationScope(IsolationScope.USER))
                // .filesystem(new RemoteFilesystemSpec(new RedisStore(redisClient)).isolationScope(IsolationScope.USER))
                .filesystem(new RemoteFilesystemSpec(
                        JdbcStore.builder(dataSource)
                                .dialect(new MysqlJdbcStoreDialect())
                                .initializeSchema(true)
                                .build())
                        .isolationScope(IsolationScope.USER))
                // .filesystem(new DockerFilesystemSpec().image("debian:13")
                //         .workspaceRoot("/workspace")
                //         .environment(Map.of("DEBUG", "true"))
                //         .memorySizeBytes(512L * 1024 * 1024)
                //         .cpuCount(2L))
                // .skillRepository(new GitSkillRepository("", false))
                .skillRepository(MysqlSkillRepository.builder(dataSource)
                        .createIfNotExist(true)
                        .writeable(true)
                        .build())
                .build();
        try {
            chat(agent, "ivfzhou", "session-1");
        } finally {
            agent.close();
            dataSource.close();
            redisClient.close();
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

    private static HikariDataSource getDataSource() {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8");
        config.setUsername("root");
        config.setPassword("123456");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }

    private static UnifiedJedis getRedisClient() {
        return RedisClient.builder()
                .hostAndPort("127.0.0.1", 6379)
                .clientConfig(DefaultJedisClientConfig.builder()
                        .user("ivfzhou")
                        .password("123456")
                        .database(0)
                        .build())
                .build();
    }

    private static Model getModel() {
        return DashScopeChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("qwen3.7-plus")
                .stream(true)
                .formatter(new DashScopeChatFormatter())
                .enableThinking(true)
                .build();
    }

    private static Toolkit getToolkit() {
        var toolkit = new Toolkit();
        toolkit.registerTool(new AfterSaleTools());
        toolkit.registerTool(new TodoTools());
        toolkit.registerMetaTool();
        return toolkit;
    }

    private static Path getWorkspace() {
        var home = Path.of(System.getProperty("user.home"), "src", "my_learning_code", "java", "agentscope");
        var agentWorkDir = Path.of(home.toAbsolutePath().toString(), "harnessagent");
        System.out.println("workspace is " + agentWorkDir);
        return agentWorkDir;
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
