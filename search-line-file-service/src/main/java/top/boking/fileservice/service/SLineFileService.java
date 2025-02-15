package top.boking.fileservice.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.boking.fileservice.domain.entity.SLineFile;
import top.boking.fileservice.mapper.SLineFileMapper;
import top.boking.fileservice.store.IFileStore;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class SLineFileService extends ServiceImpl<SLineFileMapper, SLineFile> {
    //从Spring bean中获取IFileStore的所有实现类整合到fileStores这个List中
    private final List<IFileStore> fileStores;

    public SLineFileService(List<IFileStore> fileStores) {
        this.fileStores = fileStores;
    }

    public SLineFile uploadFile(MultipartFile file) throws IOException {
        SLineFile sLineFile = buildSlineFile(file);
        //获取当前工程的resources目录
        //后续继续优化
        boolean upload = false;
        for (IFileStore fileStore : fileStores) {
            upload = fileStore.upload(sLineFile.getStoreFileName(), file.getInputStream());
            sLineFile.setStoreType(fileStore.getFileStoreType());
            if (upload) break;
        }
        if (!upload) {
            throw new RuntimeException("文件上传失败");
        }
        this.save(sLineFile);
        log.info("sLineFile:{}", sLineFile);
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
