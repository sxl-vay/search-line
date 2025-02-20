package top.boking.file.domain.entity;

import lombok.Data;
import top.boking.base.domain.entity.BaseEntity;
import top.boking.file.store.StoreType;

@Data
public class SLineFile extends BaseEntity {
    //文件名
    private String name;
    //文件上传服务
    private StoreType storeType;
    private String storePath;
    //文件后缀名
    private String suffix;
    //文件大小
    private Long fileSize;
    //文件所有者
    private Long owner;

    public String getStoreFileName() {
        return this.getId() +"-"+name;
    }
}
