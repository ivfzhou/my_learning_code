package cn.ivfzhou.java.agentscope.reactagent;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.permission.PermissionBehavior;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.permission.PermissionMode;
import io.agentscope.core.permission.PermissionRule;
import io.agentscope.core.skill.repository.AgentSkillRepository;
import io.agentscope.core.skill.repository.mysql.MysqlSkillRepository;
import io.agentscope.core.state.ConflictPolicy;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.builtin.TodoTools;
import io.agentscope.core.tool.mcp.McpClientBuilder;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.extensions.model.dashscope.credential.DashScopeCredential;
import io.agentscope.extensions.model.dashscope.formatter.DashScopeChatFormatter;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import io.agentscope.extensions.model.openai.formatter.OpenAIChatFormatter;
import io.agentscope.extensions.redis.state.RedisAgentStateStore;
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

    private static Toolkit toolkit;

    static void main() throws IOException {
        var workspace = getWorkspace();
        var dataSource = getDataSource();
        var skillRepository = createSkillRepository(dataSource);
        toolkit = createToolkit(skillRepository);
        var model = createModel();
        var fallbackModel = createFallbackModel();
        var redisClient = getRedisClient();
        // printModelCards();
        var agent = ReActAgent.builder()
                .name("answer-helper")
                .sysPrompt("你是一个全知助手，能回答各类问题。")
                // .model("dashscope:qwen-plus")
                .model(model)
                .toolkit(toolkit)
                .maxIters(20)
                .maxRetries(1)
                .fallbackModel(fallbackModel)
                .stateStore(RedisAgentStateStore.builder().jedisClient(redisClient).keyPrefix("agentscope:reactagent:").build())
                .permissionContext(
                        PermissionContextState.builder()
                                .mode(PermissionMode.DEFAULT)
                                // .addWorkingDirectory("workspace", new AdditionalWorkingDirectory(Path.of(workspace.toAbsolutePath().toString(), "workspace").toAbsolutePath().toString(), "userSettings"))
                                .build()
                )
                // .stateStore(new JsonFileAgentStateStore(workspace))
                // .middlewares(List.of(new OtelTracingMiddleware())) // 为 agent 全生命周期接入 OpenTelemetry 追踪。需要配置 OpenTelemetry SDK。
                // .middleware(new FullObservabilityMiddleware())
                // .middleware(new TimingMiddleware())
                // .middleware(new RateLimitMiddleware(Duration.ofSeconds(3)))
                // .middleware(new StopOnAllDeniedMiddleware())
                // .skillRepository(new FileSystemSkillRepository(Path.of(workspace.toAbsolutePath().toString(), "agentdir", "skills"), false))
                .skillRepository(skillRepository)
                .conflictPolicy(ConflictPolicy.FAIL)
                .skillWorkDir(Path.of(workspace.toAbsolutePath().toString(), "agentdir"))
                .build();
        try {
            chat(agent, "ivfzhou", "session-1");
            // chatWithStructure(agent, "ivfzhou", "session-2");
        } finally {
            dataSource.close();
            agent.close();
            redisClient.close();
        }
    }

    private static Model createModel() {
        return OpenAIChatModel.builder()
                // DashScopeChatModel.builder()
                .modelName("deepseek-v4-pro")
                // .modelName("qwen3.8-max")
                // .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                // .apiKey(System.getenv("OPENAI_API_KEY"))
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                // .baseUrl("https://ws-1t9uu8m17ouv3le5.cn-beijing.maas.aliyuncs.com/compatible-mode/v1")
                // .baseUrl("https://ws-1t9uu8m17ouv3le5.cn-beijing.maas.aliyuncs.com/api/v1")
                .baseUrl("https://api.deepseek.com")
                .stream(true)
                .formatter(new OpenAIChatFormatter())
                // .formatter(new DashScopeChatFormatter())
                // .enableEncrypt(true)
                // .enableThinking(true)
                // .enableSearch(true)
                // .contextWindowSize(1_000_000)
                // .nativeStructuredOutput(true)
                // .nativeStructuredOutputWithTools(true)
                // .endpointPath("/v2/chat/completions")
                // .httpTransport(JdkHttpTransport.builder().client(HttpClient.newHttpClient()).config(HttpTransportConfig.defaults()).build())
                // .httpTransport(OkHttpTransport.builder().client(new OkHttpClient.Builder().build()).build())
                // .proxy(ProxyConfig.builder().host("127.0.0.1").port(7897).type(ProxyType.HTTP).build())
                .generateOptions(GenerateOptions.builder().reasoningEffort("low").build())
                // .endpointType(EndpointType.AUTO)
                // .defaultOptions(GenerateOptions.builder().reasoningEffort("high").build())
                .build();
    }

    private static Model createFallbackModel() {
        return DashScopeChatModel.builder()
                .modelName("qwen3.7-plus")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .stream(true)
                .formatter(new DashScopeChatFormatter())
                .enableEncrypt(true)
                .enableThinking(true)
                .enableSearch(true)
                .defaultOptions(GenerateOptions.builder().reasoningEffort("low").build())
                .build();
    }

    private static AgentSkillRepository createSkillRepository(HikariDataSource dataSource) {
        return MysqlSkillRepository.builder(dataSource)
                .createIfNotExist(true)
                .writeable(true)
                .databaseName("agentscope_reactagent_skill")
                .build();
    }

    private static Toolkit createToolkit(AgentSkillRepository skillRepository) {
        var toolkit = new Toolkit();
        // toolkit.registerMcpClient(McpClientBuilder.create("amap")
        //         .streamableHttpTransport("https://mcp.amap.com/mcp?key=" + System.getenv("AMAP_API_KEY"))
        //         .buildSync()).block();
        // toolkit.registerTool(new WriteFileTool());
        // toolkit.registerTool(new ReadFileTool());
        // toolkit.registerTool(new ShellCommandTool());
        toolkit.registerTool(new TodoTools());
        toolkit.registerTool(new SkillWriterTool(skillRepository));
        toolkit.registerMetaTool();

        System.out.println("tool names is " + toolkit.getToolNames());
        System.out.println("tool schemas is " + toolkit.getToolSchemas().stream().map(ToolSchema::getName).toList());
        System.out.println("tool active groups is " + toolkit.getActiveGroups());

        // toolkit.registerToolGroup(ToolGroup.builder()
        //         .active(false)
        //         .name("my_tool_group")
        //         .scope(ToolGroupScope.META)
        //         .description("文件读写")
        //         .tools(Set.of("write_file", "read_file"))
        //         .build());
        // toolkit.createSkillToolGroup("mojibake-fixer-group", "执行 Shell 命令", false, "mojibake-fixer");
        // toolkit.registration().tool(new ShellCommandTool()).group("mojibake-fixer-group").apply();

        return toolkit;
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

    private static void printModelCards() {
        var credentialBase = DashScopeCredential.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .baseUrl("https://ws-1t9uu8m17ouv3le5.cn-beijing.maas.aliyuncs.com/api/v1")
                .id("qwen")
                .build();
        var cards = credentialBase.listModels().block();
        System.out.println("[MODEL_CARDS]");
        for (int i = 0; i < cards.size(); i++) {
            var card = cards.get(i);
            System.out.print("    " + (i + 1) + ". modelName=" + card.modelName() + " contextSize=" + card.contextSize() + " displayName=" + card.displayName());
        }
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

    private static void chatWithStructure(ReActAgent agent, String userId, String sessionId) {
        var ctx = RuntimeContext.builder()
                .userId(userId)
                .sessionId(sessionId)
                .build();
        var msg = agent.call(List.of(new UserMessage("今天天气如何？")), WeatherInfo.class, ctx).block();
        var data = msg.getStructuredData(WeatherInfo.class);
        System.out.println(data);
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

        System.out.println("enter message to chat:(quit/confirm/execute/interupt)");
        var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        var ask = reader.readLine();
        try (reader) {
            final List<ToolUseBlock> toolUseBlocks = new ArrayList<>();
            while (ask != null) {

                if (ask.equals("add_mcp")) {
                    toolkit.registerMcpClient(McpClientBuilder.create("amap")
                            .streamableHttpTransport("https://mcp.amap.com/mcp?key=" + System.getenv("AMAP_API_KEY"))
                            .buildSync()).block();
                    ask = reader.readLine();
                    continue;
                }

                if (ask.equals("add_tools")) {
                    toolkit.registerTool(new WriteFileTool());
                    toolkit.registerTool(new ReadFileTool());
                    toolkit.registerTool(new ShellCommandTool());
                    ask = reader.readLine();
                    continue;
                }

                if (ask.equalsIgnoreCase("quit")) {
                    break;
                }

                if (ask.equalsIgnoreCase("interrupt")) {
                    // 中断该 session 正在进行的 call。
                    agent.interrupt(ctx);
                    continue;
                }

                if (ask.equalsIgnoreCase("interrupt with recovery message")) {
                    // 带消息中断——中断消息会被 LLM 在恢复时看到。
                    agent.interrupt(ctx, new UserMessage("用户已取消操作"));
                    continue;
                }

                Msg msg;
                if (ask.equalsIgnoreCase("confirm") && !toolUseBlocks.isEmpty()) {
                    var confirmResults = new ArrayList<ConfirmResult>();
                    for (var i = 0; i < toolUseBlocks.size(); i++) {
                        var toolUseBlock = toolUseBlocks.get(i);
                        System.out.print("allow to run tool " + toolUseBlock.getName() + " [" + (i + 1) + "]: (y/n)");
                        var ret = reader.readLine();
                        if (ret.equalsIgnoreCase("y")) {
                            confirmResults.add(new ConfirmResult(
                                            true,
                                            toolUseBlock,
                                            List.of(new PermissionRule(toolUseBlock.getName(), null, PermissionBehavior.ALLOW, "userSettings"))
                                    )
                            );
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
                    for (var i = 0; i < toolUseBlocks.size(); i++) {
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

    private static Path getWorkspace() {
        var home = Path.of(System.getProperty("user.home"), "src", "my_learning_code", "java", "agentscope");
        var agentWorkDir = Path.of(home.toAbsolutePath().toString(), "reactagent");
        System.out.println("workspace is " + agentWorkDir);
        return agentWorkDir;
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
        System.out.println("[AGENT_START]");
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
