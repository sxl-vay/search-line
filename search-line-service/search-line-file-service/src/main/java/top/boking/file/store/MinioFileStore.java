package top.boking.file.store;

import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.utils.MinioUtils;

import java.io.File;

@Service
@Slf4j
public class MinioFileStore implements IFileStore {
    @Value("${minio.bucketName}")
    private String bucket;

    private final MinioUtils minioUtils;

    public MinioFileStore(MinioUtils minioUtils) {
        this.minioUtils = minioUtils;
    }

    @Override
    public boolean upload(SLineFile sLineFile, MultipartFile file) {
        // 上传文件
        try {
            ObjectWriteResponse response = minioUtils.uploadFile(sLineFile.getStoreFileName(), file.getInputStream(), file.getSize(), sLineFile.getGmtCreate());
            String url = minioUtils.generatePresignedUrl(sLineFile.getName(), sLineFile.getGmtCreate(), sLineFile.getStoreFileName(), -1, null);
            sLineFile.setStorePath(url);
        } catch (Exception e) {
            log.error("minio上传文件失败", e);
            return false;
        }
        return true;
    }

    @Override
    public StoreType getFileStoreType() {
        return StoreType.MINIO;
    }

    @Override
    public String getFileStorePath() {
        return "";
    }

    @Override
    public File getFile(SLineFile sLineFile) {
        return null;
    }


}
