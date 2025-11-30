package cn.ivfzhou.java.rocketmq.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

public final class Consumer {

    private static final String NAME_SERVER_ADDR =
            "ivfzhoudockerrocketmqnameserver0:9876;ivfzhoudockerrocketmqnameserver1:9876";

    public static final String GROUP = "default";

    public static void main(String[] args) throws Exception {
        consumeAssign();
    }

    static void consumeSync() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);
        consumer.subscribe("topic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                msgs.forEach(v -> System.out.println(new String(v.getBody())));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

    static void consumeCluster() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);
        consumer.subscribe("topic", "*");
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                msgs.forEach(v -> System.out.println(new String(v.getBody())));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

    static void consumeBroadcast() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);
        consumer.subscribe("topic", "*");
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                msgs.forEach(v -> System.out.println(new String(v.getBody())));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

    static void consumeOrder() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);
        consumer.subscribe("topic", "*");
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list,
                                                       ConsumeOrderlyContext consumeOrderlyContext) {
                list.forEach(v -> System.out.println(new String(v.getBody())));
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
    }

    static void consumeFilter() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);
        consumer.subscribe("topic", "tag||tags||tag1");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                msgs.forEach(v -> System.out.println(new String(v.getBody())));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

    static void consumeFilterSQL92() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);
        consumer.subscribe("topic", MessageSelector.bySql("TAGS = 'tag' and name in ('value1', 'value2')"));
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                msgs.forEach(v -> System.out.println(new String(v.getBody())));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

    static void consumePull() throws MQClientException {
        DefaultLitePullConsumer consumer = new DefaultLitePullConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);
        consumer.setPullBatchSize(20);
        consumer.subscribe("topic", "*");
        consumer.start();

        while (true) {
            List<MessageExt> list = consumer.poll();
            list.forEach(v -> System.out.println(new String(v.getBody())));
        }
        // consumer.shutdown();
    }

    static void consumeAssign() throws MQClientException {
        DefaultLitePullConsumer consumer = new DefaultLitePullConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);
        consumer.setAutoCommit(false);
        consumer.start();
        List<MessageQueue> queues = new ArrayList<>(consumer.fetchMessageQueues("topic"));
        consumer.assign(queues);
        //consumer.seek();
        while (true) {
            List<MessageExt> list = consumer.poll();
            list.forEach(v -> System.out.println(new String(v.getBody())));
            consumer.commitSync();
        }
        //consumer.shutdown();
    }

}
