package cn.ivfzhou.java.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FanoutListener {

    @RabbitHandler
    @RabbitListener(queues = "fanout_queue1")
    public void handle1(String str) {
        System.out.println("fanout_queue1监听到：" + str);
    }

    @RabbitHandler
    @RabbitListener(queues = "fanout_queue2")
    public void handle2(String str) {
        System.out.println("fanout_queue2监听到：" + str);
    }

}
