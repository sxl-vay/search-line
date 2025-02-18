package top.boking.fileservice.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.boking.fileservice.domain.entity.SLineFile;
import top.boking.fileservice.mapper.SLineFileMapper;
import top.boking.fileservice.mq.msgholder.TransactionHolder;

import java.io.IOException;

@Service
@Slf4j
public class SLineFileService extends ServiceImpl<SLineFileMapper, SLineFile> {
    //从Spring bean中获取IFileStore的所有实现类整合到fileStores这个List中
    private final FileMessageService fileMessageService;

    public SLineFileService(FileMessageService fileMessageService) {
        this.fileMessageService = fileMessageService;
    }

    public SLineFile uploadFile(MultipartFile file) throws IOException {
        SLineFile sLineFile = buildSlineFile(file);
        //获取当前工程的resources目录
        //后续继续优化

        TransactionHolder.setMultipartFile(file);
        try {
            log.info("事务消息发送中，sLineFile:{}", sLineFile);
            fileMessageService.sendTransactionMessage(sLineFile);
        } catch (Exception e) {
            log.error("事务消息发送失败，回滚文件记录，sLineFile:{}", sLineFile);
            // 如果事务消息发送失败，则回滚文件记录
            this.removeById(sLineFile.getId());
            throw e;
        } finally {
            log.info("事务消息发送完成，清理事务消息，sLineFile:{}", sLineFile);
            TransactionHolder.clear();
        }
        // 发送事务消息，确保文件记录的保存和消息发送的原子性
        log.info("文件上传成功，事务消息已发送，sLineFile:{}", sLineFile);
        return sLineFile;

    }


    private static SLineFile buildSlineFile(MultipartFile file) {
        long id = IdWorker.getId();
        log.info("IdWorker get:{}",id);
        SLineFile sLineFile = new SLineFile();
        sLineFile.setId(id);
        sLineFile.setName(file.getOriginalFilename());
        sLineFile.setOwner(1L);
        sLineFile.setFileSize(file.getSize());
        sLineFile.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        return sLineFile;
    }
}
