package top.boking.file.store;


import org.springframework.web.multipart.MultipartFile;
import top.boking.file.domain.entity.SLineFile;

import java.io.File;
import java.io.InputStream;

public interface IFileStore {

    boolean upload(SLineFile sLineFile, MultipartFile fileStream);
    StoreType getFileStoreType();
    String getFileStorePath();
    File getFile(SLineFile sLineFile);
}
