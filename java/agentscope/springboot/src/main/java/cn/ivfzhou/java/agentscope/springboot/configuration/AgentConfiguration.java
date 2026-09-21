package cn.ivfzhou.java.agentscope.springboot.configuration;

import io.agentscope.core.model.Model;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import io.agentscope.extensions.model.openai.formatter.OpenAIChatFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {

    @Bean
    public Model model() {
        return OpenAIChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("qwen3.7-plus")
                .stream(true)
                .formatter(new OpenAIChatFormatter())
                .baseUrl("https://ws-1t9uu8m17ouv3le5.cn-beijing.maas.aliyuncs.com/compatible-mode/v1")
                .build();
    }

}
