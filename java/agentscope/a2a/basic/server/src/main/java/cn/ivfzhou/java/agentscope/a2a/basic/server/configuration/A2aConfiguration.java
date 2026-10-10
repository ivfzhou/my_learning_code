package cn.ivfzhou.java.agentscope.a2a.basic.server.configuration;

import io.a2a.spec.TransportProtocol;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.a2a.server.AgentScopeA2aServer;
import io.agentscope.core.a2a.server.card.ConfigurableAgentCard;
import io.agentscope.core.a2a.server.transport.TransportProperties;
import io.agentscope.core.model.Model;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.mcp.McpClientBuilder;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import io.agentscope.extensions.model.openai.formatter.OpenAIChatFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class A2aConfiguration {

    public static final String JSON_RPC_PATH = "/a2a";

    @Value("${a2a.server.host:localhost}")
    private String host;

    @Value("${server.port:8080}")
    private int port;

    @Bean
    public Model model() {
        return OpenAIChatModel.builder()
                .modelName("qwen-plus-2025-07-28")
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .baseUrl("https://ws-1t9uu8m17ouv3le5.cn-beijing.maas.aliyuncs.com/compatible-mode/v1")
                .stream(true)
                .formatter(new OpenAIChatFormatter())
                .build();
    }

    @Bean
    public ReActAgent.Builder agentBuilder(Model model) {
        return ReActAgent.builder()
                .name("a2a-demo-agent")
                .sysPrompt("你是一个通过 A2A 协议对外提供服务的助手。")
                .model(model)
                .toolkit(toolkit())
                .maxIters(10);
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
    public AgentScopeA2aServer a2aServer(ReActAgent.Builder agentBuilder) {
        return AgentScopeA2aServer.builder(agentBuilder)
                .agentCard(new ConfigurableAgentCard.Builder()
                        .name("a2a-demo-agent")
                        .description("AgentScope 2.x A2A Server 示例")
                        .version("1.0.0")
                        .build())
                .withTransport(TransportProperties.builder(TransportProtocol.JSONRPC.asString())
                        .host(host)
                        .port(port)
                        .path(JSON_RPC_PATH)
                        .build())
                .build();
    }

    /**
     * 应用启动就绪后的回调方法。
     * 当 Spring 容器完全初始化完成并发布 ApplicationReadyEvent 事件时，
     * 自动触发本方法，通知 A2A 服务器端点已就绪、可以开始对外提供服务。
     *
     * @param event 应用就绪事件，可用于获取已刷新完成的 ApplicationContext。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void postEndpointReady(ApplicationReadyEvent event) {
        event.getApplicationContext().getBean(AgentScopeA2aServer.class).postEndpointReady();
    }

}
