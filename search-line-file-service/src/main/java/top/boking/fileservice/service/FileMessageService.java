package top.boking.fileservice.service;

import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import top.boking.fileservice.domain.entity.SLineFile;

@Service
public class FileMessageService {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final String FILE_TRANSACTION_TOPIC = "file-transaction-topic";

    public TransactionSendResult sendTransactionMessage(SLineFile file) {
        // 构建事务消息
        Message<SLineFile> message = MessageBuilder.withPayload(file).build();
        // 发送事务消息，将文件对象作为参数传递给事务监听器
        return rocketMQTemplate.sendMessageInTransaction(FILE_TRANSACTION_TOPIC, message, file);
    }
}