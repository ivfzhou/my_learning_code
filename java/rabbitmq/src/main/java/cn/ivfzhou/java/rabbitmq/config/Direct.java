package cn.ivfzhou.java.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Direct {

    @Bean
    public Queue directQueue1() {
        return new Queue("direct_queue1", false, false, false);
    }

    @Bean
    public Queue directQueue2() {
        return new Queue("direct_queue2", false, false, false);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct_exchange", false, false);
    }

    @Bean
    public Binding bindDirectQueue1(@Qualifier("directQueue1") Queue directQueue1, DirectExchange directExchange) {
        return BindingBuilder.bind(directQueue1).to(directExchange).withQueueName();
    }

    @Bean
    public Binding bindDirectQueue2(@Qualifier("directQueue2") Queue directQueue2, DirectExchange directExchange) {
        return BindingBuilder.bind(directQueue2).to(directExchange).withQueueName();
    }

}
