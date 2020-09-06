package com.l1fe1.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class SyncProducer {
    public static void main(String[] args) throws Exception {
        // 使用生产者组名实例化 DefaultMQProducer
        DefaultMQProducer producer = new
                DefaultMQProducer("producerGroup1");
        // 指定 name server 的地址
        producer.setNamesrvAddr("192.168.114.60:9876");
        // 启动实例
        producer.start();
        for (int i = 0; i < 100; i ++) {
            // 创建一个消息实例，指定 topic，tag和消息体
            Message msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            // 调用发送消息的接口将消息发送到一个 broker 上
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        // 关闭生产者实例
        producer.shutdown();
    }
}
