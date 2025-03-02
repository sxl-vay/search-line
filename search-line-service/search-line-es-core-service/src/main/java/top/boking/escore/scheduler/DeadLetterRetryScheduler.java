package top.boking.escore.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.boking.escore.consts.ESMQConst;
import top.boking.escore.entity.DeadLetterRecord;
import top.boking.escore.repository.DeadLetterRecordRepository;
import top.boking.file.consts.MQConst;
import com.alibaba.fastjson.JSON;
import top.boking.file.domain.entity.SLineFile;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DeadLetterRetryScheduler {
    @Value("${rocketmq.name-server}")
    private String nameServer;
    @Autowired
    private DefaultLitePullConsumer pullConsumer;

    @Autowired
    private DefaultMQProducer producer;

    @Autowired
    private DeadLetterRecordRepository deadLetterRecordRepository;

    private static final int MAX_RETRY_TIMES = 3;

    // 每 5 分钟扫描一次死信队列
    @Scheduled(cron = "0 */1 * * * ?")
    public void retryDeadLetterMessages() {
        pullConsumer.setPullBatchSize(1);
        List<MessageExt> messages = pullConsumer.poll(1000);
        if (!messages.isEmpty()) {
            for (MessageExt msg : messages) {
                String msgStr = new String(msg.getBody());
                log.error("拉取到消息: " + msgStr);
                SLineFile sLineFile = null;
                try {
                    sLineFile = JSON.parseObject(msgStr, SLineFile.class);
                    log.info("消息体转换为SLineFile对象成功: {}", sLineFile);
                } catch (Exception e) {
                    log.error("消息体转换为SLineFile对象失败: {}", e.getMessage());
                }
                // 记录异常信息
                DeadLetterRecord record = new DeadLetterRecord();
                record.setMessageId(msg.getMsgId());
                record.setMessageBody(msgStr);
                record.setTopic(msg.getTopic());
                record.setRetryTimes(msg.getReconsumeTimes());
                record.setErrorMessage("消息重试处理中");
                deadLetterRecordRepository.insert(record);
//                retryMes(msg);
            }
        }
    }

    private void retryMes(MessageExt msg) {
        Map<String, String> properties = msg.getProperties();
        String retryTopic = properties.get("RETRY_TOPIC");
        if (!MQConst.FILE_TRANSACTION_TOPIC.equals(retryTopic)) {
            return;
        }
        // 重新投递消息到原始队列
        Message newMsg = new Message(retryTopic, msg.getTags(), msg.getKeys(), msg.getBody());
        try {
            SendResult sendResult = producer.send(newMsg);
            log.info("消息重新投递成功, msgId={}, sendResult={}", msg.getMsgId(), sendResult);
        } catch (Exception e) {
            log.error("消息重新投递失败, msgId={}, error={}", msg.getMsgId(), e.getMessage());
            rollback(msg);
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