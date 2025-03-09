package top.boking.file.scheduler;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import top.boking.file.consts.MQConst;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.mapper.SLineFileMapper;

import java.util.List;

@Component
public class FileDeleteScheduler {

    private static final Logger log = LoggerFactory.getLogger(FileDeleteScheduler.class);

    @Autowired
    private SLineFileMapper sLineFileMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    //定期每天12点清理过期文件
    @Scheduled(cron = "0 */1 * * * ?")
    public void clearExpiredFiles() {
        //开启一个编程式事务
        Boolean execute = transactionTemplate.execute(status -> {
            try {
                // 在此处编写数据库操作（如 JPA、JDBC、MyBatis 等）
                List<SLineFile> list = sLineFileMapper.selectWithDeleteType(1);
                List<String> fileNames = list.stream().map(SLineFile::getStoreFileName).toList();
                List<Long> fileIds = list.stream().map(SLineFile::getId).toList();
                if (fileIds.isEmpty()) {
                    return true;
                }
                Message<List<Long>> message = MessageBuilder.withPayload(fileIds).build();
                LambdaUpdateWrapper<SLineFile> lambda = new UpdateWrapper<SLineFile>().lambda();
                lambda.set(SLineFile::getDeleted, 2);
                lambda.in(SLineFile::getId, fileIds);
                //更新状态为已删除
                sLineFileMapper.batchDelete2(fileIds);
                //发送事务消息到es
                TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(MQConst.FILE_DELETE_2_ES_TOPIC, message, list);
                log.info("事务消息发送结果：{}", list);
                if (!transactionSendResult.getLocalTransactionState().equals(LocalTransactionState.COMMIT_MESSAGE)) {
                    throw new RuntimeException("事务消息发送失败，回滚文件记录");
                }
                // 其他业务逻辑...
                return true; // 提交事务
            } catch (Exception e) {
                status.setRollbackOnly(); // 标记回滚
                return false;
            }
        });

    }
}