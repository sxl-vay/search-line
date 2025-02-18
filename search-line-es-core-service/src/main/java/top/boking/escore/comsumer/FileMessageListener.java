package top.boking.escore.comsumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        consumerGroup = "es-core", topic = ("file-transaction-topic"))
@Slf4j
public class FileMessageListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String event) {
        //todo 将文件服务消息中文件内容添加到es中
        log.info("es:接收到文件服务消息：{}", event);
    }
}


