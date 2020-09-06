package com.l1fe1.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

public class Consumer {
    public static void main(String[] args) throws MQClientException {

        // 使用消费者组名实例化 DefaultMQPushConsumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerGroup1");

        // 指定 name server 的地址
        consumer.setNamesrvAddr("192.168.114.60:9876");

        // 订阅某个 topic 进行消费，第二个参数为过滤器，* 表示不过滤
        consumer.subscribe("TopicTest", "*");
        // 注册回调以在从 broker 获取的消息到达时执行
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
            // 默认情况下，这条消息只会被一个 consumer 消费（点到点）
            // broker 端会对 message 进行状态修改
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // 启动消费者实例
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
