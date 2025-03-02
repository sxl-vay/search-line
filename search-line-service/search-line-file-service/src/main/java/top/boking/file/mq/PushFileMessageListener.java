package top.boking.file.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.mq.msgholder.TransactionHolder;
import top.boking.file.service.SLineFileService;
import top.boking.file.store.IFileStore;

@Slf4j
@Component
@RocketMQTransactionListener
public class PushFileMessageListener implements RocketMQLocalTransactionListener {

    private final SLineFileService sLineFileService;
    private final IFileStore fileStore;

    public PushFileMessageListener(SLineFileService sLineFileService, IFileStore fileStores) {
        this.sLineFileService = sLineFileService;
        this.fileStore = fileStores;
    }


    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        SLineFile sLineFile = (SLineFile) arg;
        log.info("事务消息执行本地事务，文件ID：{}",sLineFile.getId());
        MultipartFile file = TransactionHolder.getMultipartFileHolder(); //todo 获取对象
        if (file == null) {
            log.error("事务消息为空，事务回滚");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        sLineFileService.save(sLineFile);
        boolean upload = fileStore.upload(sLineFile, file);
        sLineFileService.updateById(sLineFile);
        sLineFile.setStoreType(fileStore.getFileStoreType());
        sLineFile.setStorePath(fileStore.getFileStorePath());
        if (!upload) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        // 执行本地事务：保存文件记录
        log.info("本地事务执行成功，文件ID：{}", sLineFile.getId());

        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        try {
            // 从消息中获取文件ID
            String fileId = new String((byte[]) msg.getPayload());
            // 查询本地事务执行结果
            SLineFile file = sLineFileService.getById(Long.parseLong(fileId));
            log.info("事务消息触发本地回查：{}",fileId);
            return file != null ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
        } catch (Exception e) {
            log.error("事务状态回查失败", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}