package top.boking.escore.comsumer.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.boking.escore.consts.ESMQConst;
import top.boking.escore.repository.KnowledgeBaseRepository;
import top.boking.file.consts.MQConst;

import java.util.List;

@Component
@RocketMQMessageListener(
        consumerGroup = ESMQConst.FILE_DEL_COMSUMERGROUP
        , topic = (MQConst.FILE_DELETE_2_ES_TOPIC)
        , maxReconsumeTimes = 1
)
@Slf4j
public class FileDeleteMessageListener implements RocketMQListener<List<String>> {

    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;


    @Override
    public void onMessage(List<String> message) {
        knowledgeBaseRepository.deleteAllById(message);
        log.info("es:接收到文件服务消息：{}", message);
    }
}
