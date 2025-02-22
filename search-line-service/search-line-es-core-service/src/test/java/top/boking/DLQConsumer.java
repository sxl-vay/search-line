package top.boking;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

public class DLQConsumer {
    public static void main(String[] args) throws Exception {
        // 初始化消费者（需唯一消费者组名）
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("dlq_consumer_group");
        consumer.setNamesrvAddr("localhost:9876"); // NameServer 地址

        // 订阅死信队列（以消费者组 my_consumer_group 为例）
        consumer.subscribe("%DLQ%my_consumer_group", "*"); // "*" 表示所有 Tag

        // 注册消息监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.println("收到死信消息: " + new String(msg.getBody()));
                // 可在此处理死信消息（如记录日志、人工干预等）
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // 启动消费者
        consumer.start();
        System.out.println("死信队列消费者已启动");
    }
}