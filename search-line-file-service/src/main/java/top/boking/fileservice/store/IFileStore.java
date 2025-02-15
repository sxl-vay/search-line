package top.boking.fileservice.store;

import top.boking.fileservice.domain.entity.SLineFile;

import java.io.File;
import java.io.InputStream;

public interface IFileStore {

    boolean upload(String name, InputStream fileStream);
    String getFileStoreType();
    File getFile(SLineFile sLineFile);
}
