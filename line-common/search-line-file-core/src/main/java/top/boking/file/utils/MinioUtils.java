package top.boking.file.utils;

import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class MinioUtils {
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

    public ObjectWriteResponse uploadFile(String fileName, InputStream file, long fileSize)
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

    public GetObjectResponse getFile(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException
            , InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
    }




}
