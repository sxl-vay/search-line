package top.boking.file.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.mq.msgholder.TransactionHolder;

import java.io.IOException;

@Service
@Slf4j
public class SLineFileService extends SLineFileCoreService {
    //从Spring bean中获取IFileStore的所有实现类整合到fileStores这个List中
    private final FileMessageService fileMessageService;

    public SLineFileService(FileMessageService fileMessageService) {
        this.fileMessageService = fileMessageService;
    }

    @Transactional
    public SLineFile uploadFile(MultipartFile file) throws IOException {
        SLineFile sLineFile = buildSlineFile(file);
        //获取当前工程的resources目录
        //后续继续优化
        if (!file.isEmpty()) {
            log.info("文件上传中，sLineFile:{}", sLineFile);
            //todo 优化
            //获取文件名
            String fileName = file.getOriginalFilename();
            //获取文件后缀
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //todo 优化
            //获取文件大小
            long fileSize = file.getSize();
            //todo 优化
            //获取文件类型
            String contentType = file.getContentType();
        }
        TransactionHolder.setMultipartFile(file);
        try {
            log.info("事务消息发送中，sLineFile:{}", sLineFile);
            TransactionSendResult transactionSendResult = fileMessageService.sendTransactionMessage(sLineFile);
            if (transactionSendResult.getLocalTransactionState().equals(LocalTransactionState.ROLLBACK_MESSAGE)) {
                throw new RuntimeException("事务消息发送失败，回滚文件记录");
            }
        } catch (Exception e) {
            log.error("事务消息发送失败，回滚文件记录，sLineFile:{}", sLineFile);
            // 如果事务消息发送失败，则回滚文件记录
//            this.removeById(sLineFile.getId());
            throw e;
        } finally {
            log.info("事务消息发送完成，清理事务消息，sLineFile:{}", sLineFile);
            TransactionHolder.clear();
        }
        // 发送事务消息，确保文件记录的保存和消息发送的原子性
        log.info("文件上传成功，事务消息已发送，sLineFile:{}", sLineFile);
        return sLineFile;
    }

    /**
     * 根据上传文件构建SLineFile实体对象
     *
     * @param file 上传的MultipartFile文件对象
     * @return 包含文件基本属性的SLineFile实体
     */
    private static SLineFile buildSlineFile(MultipartFile file) {
        // 生成唯一ID并记录日志
        long id = IdWorker.getId();
        log.info("IdWorker get:{}",id);
        // 构建实体对象并填充属性
        SLineFile sLineFile = new SLineFile();
        sLineFile.setId(id);
        sLineFile.setName(file.getOriginalFilename());
        sLineFile.setOwner(1L);
        sLineFile.setFileSize(file.getSize());
        sLineFile.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));

        return sLineFile;
    }

}
