package top.boking.file.store;


import top.boking.file.domain.entity.SLineFile;

import java.io.File;
import java.io.InputStream;

public interface IFileStore {

    boolean upload(String name, InputStream fileStream);
    StoreType getFileStoreType();
    String getFileStorePath();
    File getFile(SLineFile sLineFile);
}
