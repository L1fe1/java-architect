package com.l1fe1.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BatchProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new
                DefaultMQProducer("producerGroup1");
        producer.setNamesrvAddr("192.168.114.60:9876");
        producer.start();
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 100; i ++) {
            Message msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            messages.add(msg);
        }
        // 批量发送消息
        SendResult sendResult = producer.send(messages);
        System.out.printf("%s%n", sendResult);

        // 将大的 list 拆分成小的 list
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            try {
                List<Message>  listItem = splitter.next();
                producer.send(listItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 关闭生产者实例
        producer.shutdown();
    }
}

class ListSplitter implements Iterator<List<Message>> {
    private final int SIZE_LIMIT = 1000 * 1000;
    private final List<Message> messages;
    private int currIndex;
    public ListSplitter(List<Message> messages) {
        this.messages = messages;
    }
    @Override public boolean hasNext() {
        return currIndex < messages.size();
    }
    @Override public List<Message> next() {
        int nextIndex = currIndex;
        int totalSize = 0;
        for (; nextIndex < messages.size(); nextIndex++) {
            Message message = messages.get(nextIndex);
            int tmpSize = message.getTopic().length() + message.getBody().length;
            Map<String, String> properties = message.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                tmpSize += entry.getKey().length() + entry.getValue().length();
            }
            tmpSize = tmpSize + 20; // 日志开销
            if (tmpSize > SIZE_LIMIT) {
                // 意外情况下的单个消息大小超出了 SIZE_LIMIT 限制
                // 这种情况下让它跳过，否则会阻碍拆分过程
                if (nextIndex - currIndex == 0) {
                    // 如果下一个子列表中没有元素，则添加此元素，然后跳过，否则直接跳过
                    nextIndex ++;
                }
                break;
            }
            if (tmpSize + totalSize > SIZE_LIMIT) {
                break;
            } else {
                totalSize += tmpSize;
            }

        }
        List<Message> subList = messages.subList(currIndex, nextIndex);
        currIndex = nextIndex;
        return subList;
    }
}
