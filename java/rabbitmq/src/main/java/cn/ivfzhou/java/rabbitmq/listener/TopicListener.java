package cn.ivfzhou.java.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TopicListener {

    @RabbitHandler
    @RabbitListener(queues = "topic_queue1")
    public void handle1(String str) {
        System.out.println("topic_queue1监听到：" + str);
    }

    @RabbitHandler
    @RabbitListener(queues = "topic_queue2")
    public void handle2(String str) {
        System.out.println("topic_queue2监听到：" + str);
    }

}
