package top.boking.escore.domain.entity;

import lombok.Data;
import top.boking.base.domain.entity.BaseEntity;

@Data
public class FileTransferRecord extends BaseEntity {
    private Long fileId;

    public static FileTransferRecord create(Long fileId) {
        FileTransferRecord fileTransferRecord = new FileTransferRecord();
        fileTransferRecord.setFileId(fileId);
        return fileTransferRecord;
    }
}
