package cn.ivfzhou.java.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Fanout {

    @Bean
    public Queue fanoutQueue1() {
        return new Queue("fanout_queue1", false, false, false);
    }

    @Bean
    public Queue fanoutQueue2() {
        return new Queue("fanout_queue2", false, false, false);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout_exchange", false, false);
    }

    @Bean
    public Binding bindFanoutQueue1(@Qualifier("fanoutQueue1") Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public Binding bindFanoutQueue2(@Qualifier("fanoutQueue2") Queue fanoutQueue2, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }

}
