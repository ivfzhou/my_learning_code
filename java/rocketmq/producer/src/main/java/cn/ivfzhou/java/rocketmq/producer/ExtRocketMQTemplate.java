package cn.ivfzhou.java.rocketmq.producer;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

@ExtRocketMQTemplateConfiguration(namespace = "${rocketmq.name-server}")
public class ExtRocketMQTemplate extends RocketMQTemplate {
}
