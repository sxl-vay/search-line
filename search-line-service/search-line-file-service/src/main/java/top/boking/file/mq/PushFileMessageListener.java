package top.boking.file.mq;

import com.alibaba.fastjson.JSON;
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

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RocketMQTransactionListener
public class PushFileMessageListener implements RocketMQLocalTransactionListener {

    private final long timeout = 3000_000;

    private final SLineFileService sLineFileService;
    private final IFileStore fileStore;

    public PushFileMessageListener(SLineFileService sLineFileService, IFileStore fileStores) {
        this.sLineFileService = sLineFileService;
        this.fileStore = fileStores;
    }

    private final Set<Long> processingFiles = new HashSet<>();

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        long startTime = System.currentTimeMillis();
        SLineFile sLineFile = (SLineFile) arg;
        processingFiles.add(sLineFile.getId());
        try {
            log.info("事务消息执行本地事务，文件ID：{}",sLineFile.getId());
            MultipartFile file = TransactionHolder.getMultipartFileHolder();
            if (file == null) {
                log.error("事务消息为空，事务回滚");
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            sLineFile.setStoreType(fileStore.getFileStoreType());
            sLineFile.setStorePath(fileStore.getFileStorePath());
            boolean upload = fileStore.upload(sLineFile, file);
            if (!upload) {
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            // 执行本地事务：保存文件记录
            log.info("本地事务执行成功，文件ID：{}", sLineFile.getId());
        } finally {
            processingFiles.remove(sLineFile.getId());
        }
        long spentTime = System.currentTimeMillis() - startTime;
        if (spentTime > timeout) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        try {
            SLineFile sLineFile = JSON.parseObject(new String((byte[]) msg.getPayload()), SLineFile.class);
            Long fileId = sLineFile.getId();
            if (processingFiles.contains(fileId)) {
                log.info("事务消息正在处理中，文件ID：{}", fileId);
                return RocketMQLocalTransactionState.UNKNOWN;
            }
            // 查询本地事务执行结果
            SLineFile file = sLineFileService.getById(fileId);
            log.info("事务消息触发本地回查：{}",file);
            return file.getStorePath() != null ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.UNKNOWN;
        } catch (Exception e) {
            log.error("事务状态回查失败", e);
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}