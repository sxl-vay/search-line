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
import top.boking.file.service.SLineFileCoreService;
import top.boking.file.service.SLineFileService;
import top.boking.file.store.IFileStore;

import java.util.List;

@Slf4j
@Component
@RocketMQTransactionListener
public class PushFileMessageListener implements RocketMQLocalTransactionListener {

    private final SLineFileService sLineFileService;
    private final List<IFileStore> fileStores;

    public PushFileMessageListener(SLineFileService sLineFileService, List<IFileStore> fileStores) {
        this.sLineFileService = sLineFileService;
        this.fileStores = fileStores;
    }


    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            log.info("事务消息执行本地事务，文件ID：{}", msg.getPayload());
            MultipartFile file = TransactionHolder.getMultipartFileHolder(); //todo 获取对象
            if (file == null) {
                log.error("事务消息为空，事务回滚");
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            SLineFile sLineFile = (SLineFile) arg;
            boolean upload = false;
            for (IFileStore fileStore : fileStores) {
                upload = fileStore.upload(sLineFile.getStoreFileName(), file.getInputStream());
                sLineFile.setStoreType(fileStore.getFileStoreType());
                sLineFile.setStorePath(fileStore.getFileStorePath());
                if (upload) break;
            }
            if (!upload) {
                throw new RuntimeException("文件上传失败");
            }
            // 执行本地事务：保存文件记录
            sLineFileService.save(sLineFile);
            log.info("本地事务执行成功，文件ID：{}", sLineFile.getId());
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.error("本地事务执行失败", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        try {
            // 从消息中获取文件ID
            String fileId = new String((byte[]) msg.getPayload());
            // 查询本地事务执行结果
            SLineFile file = sLineFileService.getById(Long.parseLong(fileId));
            return file != null ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
        } catch (Exception e) {
            log.error("事务状态回查失败", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}