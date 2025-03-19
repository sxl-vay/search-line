package top.boking.file.store;


import top.boking.file.domain.entity.SLineFile;

import java.io.File;
import java.io.InputStream;

public interface IFileStore {

    boolean upload(SLineFile sLineFile, InputStream inputStream, long size);
    StoreType getFileStoreType();
    String getFileStorePath();
    File getFile(SLineFile sLineFile);
}
