package top.boking.fileservice.service;

import java.io.InputStream;

/**
 * 定义文件服务接口
 */
public interface IFileService {
    boolean upload(String name, InputStream fileStream);

}
