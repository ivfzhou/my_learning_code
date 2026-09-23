package cn.ivfzhou.java.agentscope.a2a.server;

import io.a2a.spec.TransportProtocol;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.a2a.server.AgentScopeA2aServer;
import io.agentscope.core.a2a.server.card.ConfigurableAgentCard;
import io.agentscope.core.a2a.server.transport.TransportProperties;
import io.agentscope.core.model.Model;
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
                .modelName("qwen3.7-plus")
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
                .maxIters(10);
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

    @EventListener(ApplicationReadyEvent.class)
    public void postEndpointReady(ApplicationReadyEvent event) {
        event.getApplicationContext().getBean(AgentScopeA2aServer.class).postEndpointReady();
    }

}
