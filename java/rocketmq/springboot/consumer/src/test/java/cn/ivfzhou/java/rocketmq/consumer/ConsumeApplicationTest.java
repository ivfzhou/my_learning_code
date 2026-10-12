package cn.ivfzhou.java.rocketmq.consumer;

import javax.annotation.Resource;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConsumeApplicationTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Test
    void test() {
        System.out.println(rocketMQTemplate.receive(String.class));
    }

}