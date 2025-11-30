package cn.ivfzhou.java.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SampleListener {

    @RabbitHandler
    @RabbitListener(queues = "sample_queue")
    public void handle(String str) {
        System.out.println("sample_queue监听到：" + str);
    }

}
