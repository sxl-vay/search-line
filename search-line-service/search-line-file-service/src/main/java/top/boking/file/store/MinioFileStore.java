package top.boking.file.store;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.boking.file.domain.entity.SLineFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MinioFileStore implements IFileStore{
    @Value("${minio.bucketName}")
    private String bucket;

    private final MinioClient minioClient;

    public MinioFileStore(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public boolean upload(SLineFile sLineFile, MultipartFile file) {
        // 上传文件
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(sLineFile.getStoreFileName())
                            .stream(file.getInputStream(),file.getSize(),-1) // 文件大小和分片大小
    //                        .contentType(file.getContentType())
                            .build());
            String url = generatePresignedUrl(bucket, sLineFile.getStoreFileName(), 1, TimeUnit.HOURS);
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

    /**
     * 生成预签名链接
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expiry     有效期
     * @param unit       时间单位
     * @return 预签名链接
     */
    public String generatePresignedUrl(String bucketName, String objectName, int expiry, TimeUnit unit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
//                            .expiry(expiry, unit)
                            .build());
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("生成预签名链接失败", e);
        }
    }
}
