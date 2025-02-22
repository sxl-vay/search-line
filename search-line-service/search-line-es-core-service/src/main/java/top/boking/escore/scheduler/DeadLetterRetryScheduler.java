package top.boking.escore.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DeadLetterRetryScheduler {
    @Value("${rocketmq.name-server}")
    private String nameServer;
    @Autowired
    private DefaultLitePullConsumer pullConsumer;

    private static final int MAX_RETRY_TIMES = 3;

    // 每 5 分钟扫描一次死信队列
    @Scheduled(cron = "0 */1 * * * ?")
    public void retryDeadLetterMessages() {
        pullConsumer.setPullBatchSize(1);
        List<MessageExt> messages = pullConsumer.poll(1000);
        if (!messages.isEmpty()) {
            for (MessageExt msg : messages) {
                System.out.println("拉取到消息: " + new String(msg.getBody()));
                // 抛出异常以触发消息重试
//                rollback(msg);
            }
        }
    }

    private void rollback(MessageExt msg) {
        MessageQueue messageQueue = new MessageQueue(msg.getTopic(), msg.getBrokerName(), msg.getQueueId());
        try {
            pullConsumer.seek(messageQueue, msg.getQueueOffset());
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }
}