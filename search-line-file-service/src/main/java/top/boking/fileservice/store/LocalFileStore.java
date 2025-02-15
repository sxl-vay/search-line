package top.boking.fileservice.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import top.boking.fileservice.domain.entity.SLineFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Component
@Slf4j
public class LocalFileStore implements IFileStore {

    @Value("${server.port}")
    private String port;

    @Override
    public boolean upload(String name, InputStream inputStream) {
        String dirPath = getDirPath();
        File localFile = new File(dirPath + File.separator + name);
        // 通过inputStream 将文件保存到服务器本地
        if (localFile.isAbsolute() && !localFile.exists()) {
            try {
                FileCopyUtils.copy(inputStream, Files.newOutputStream(localFile.toPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public String getFileStoreType() {
        String ip = getLocalIp();
        return StoreType.LOCAL + ":" + ip + ":" + port;
    }

    @Override
    public File getFile(SLineFile sLineFile) {
        String dirPath = getDirPath();
        File file = new File(dirPath + File.separator + sLineFile.getName());
        if (file.exists()) {
            return file;
        }
        return null;
    }

    private String getLocalIp() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException e) {
            log.error("获取本机IP地址失败", e);
            return "localDefault";
        }
    }

    private static String getDirPath() {
        String path = System.getProperty("user.dir");
        String dirPath = path + File.separator + "filedir";
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            boolean mkdirs = dirFile.mkdirs();
            if (!mkdirs) {
                log.error("创建目录失败");
            } else {
                log.info("创建目录成功");
            }
        }
        return dirPath;
    }
}