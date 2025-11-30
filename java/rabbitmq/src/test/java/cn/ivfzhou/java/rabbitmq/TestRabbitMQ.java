package cn.ivfzhou.java.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestRabbitMQ {

    @Autowired
    RabbitTemplate rt;

    @Test
    public void sample_send() {
        rt.convertAndSend("sample_queue", "hello sample queue");
    }

    @Test
    public void fanout_send() {
        rt.convertAndSend("fanout_exchange", "", "hello fanout queue");
    }

    @Test
    public void direct_send() {
        rt.convertAndSend("direct_exchange", "direct_queue1", "hello direct queue");
    }

    @Test
    public void topic_send() {
        rt.convertAndSend("topic_exchange", "a.b.*", "hello topic queue");
    }

}
