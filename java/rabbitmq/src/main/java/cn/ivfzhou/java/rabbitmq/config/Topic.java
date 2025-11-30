package cn.ivfzhou.java.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Topic {

    @Bean
    public Queue topicQueue1() {
        return new Queue("topic_queue1", false, false, false);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue("topic_queue2", false, false, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic_exchange", false, false);
    }

    @Bean
    public Binding bindTopicQueue1(@Qualifier("topicQueue1") Queue topicQueue1, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueue1).to(topicExchange).with("a.*");
    }

    @Bean
    public Binding bindTopicQueue2(@Qualifier("topicQueue2") Queue topicQueue2, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueue2).to(topicExchange).with("a.b.*");
    }

}
