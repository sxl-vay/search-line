package top.boking.file.utils;

import com.google.common.collect.Lists;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtils {

    private static final Logger log = LoggerFactory.getLogger(MinioUtils.class);

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.bucketName:search-line}")
    private String bucketName;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public MinioClient minioClient() {
        return minioClient;
    }

    private static @NotNull String buildFileName(String fileName, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fileName = simpleDateFormat.format(date) + "/" + fileName;
        return fileName;
    }

    public GetObjectResponse getFile(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException
            , InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
    }

    /**
     * 使用MinIO客户端上传文件到指定存储桶
     * <p>
     * 本方法封装了MinIO客户端的putObject操作，支持大文件分片上传。当分片大小设置为-1时，
     * 由SDK自动计算最佳分片大小（通常为5MB-5GB范围）
     *
     * @param fileName 目标文件名（包含路径），示例：/user/docs/report.pdf
     * @param file     要上传的文件输入流，需确保流可读且未被关闭
     * @param fileSize 文件大小（单位：字节），用于分片计算和进度跟踪
     * @return 包含版本ID、ETag等元数据的写入响应对象
     * @throws ServerException           MinIO服务端返回5xx错误
     * @throws InsufficientDataException 输入流读取异常或数据不完整
     * @throws ErrorResponseException    MinIO服务端返回4xx错误
     * @throws IOException               网络通信或本地IO异常
     * @throws NoSuchAlgorithmException  缺少MD5或SHA-256算法支持
     * @throws InvalidKeyException       无效的S3访问密钥
     * @throws InvalidResponseException  服务端返回异常响应格式
     * @throws XmlParserException        XML解析失败
     * @throws InternalException         MinIO SDK内部错误
     */
    public ObjectWriteResponse uploadFile(String fileName, InputStream file, long fileSize, Date date)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException
            , InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file, fileSize, -1) // 文件大小和分片大小
                        //                        .contentType(file.getContentType())
                        .build());
    }

    /**
     * 生成预签名链接
     *
     * @param objectName 对象名称
     * @param expiry     有效期
     * @param unit       时间单位
     * @return 预签名链接
     */
    public String generatePresignedUrl(String fileName, Date date, String objectName, int expiry, TimeUnit unit) {
        try {
            GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .extraQueryParams(Map.of(
                            "response-content-disposition",
                            "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\""
                    ));
            if (expiry > 0) {
                builder.expiry(expiry, unit);
            }
            GetPresignedObjectUrlArgs build = builder.build();
            String url = minioClient.getPresignedObjectUrl(
                    build);
            log.info("download url:{}", url);
            return url;
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("生成预签名链接失败", e);
        }
    }

    public List<String> listFiles() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException
            , InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build());
        List<String> fileNames = Lists.newArrayList();
        for (Result<Item> result : results) {
            Item item = result.get();
            if (!item.isDir()) {
                fileNames.add(item.objectName());
            }
        }
        return fileNames;
    }

    //根据文件名称列表批量删除minio上的文件
    public void deleteFiles(List<String> fileNames) {
        List<DeleteObject> deleteObjectList = fileNames.stream().map(DeleteObject::new).toList();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(deleteObjectList).build());
        results.forEach(result -> {
            try {
                DeleteError error = result.get();
                log.error("删除文件失败", error.message());
            } catch (Exception e) {
                log.error("删除文件失败", e);
            }
        });
    }


}
