package cn.ivfzhou.java.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Sample {

    @Bean
    public Queue sampleQueue() {
        return new Queue("sample_queue", false, false, false);
    }

}
