package top.boking.file.domain.dto;

import lombok.Data;
import top.boking.file.domain.entity.SLineFile;

import java.text.SimpleDateFormat;

@Data
public class SlineFileDTO {
    private String id;
    private String name;
    private String fileSize;
    private String type;
    private String url;
    private String createTime;

    public static SlineFileDTO buildWithSLineFile(SLineFile sLineFile) {
        SlineFileDTO slineFileDTO = new SlineFileDTO();
        slineFileDTO.setId(sLineFile.getId().toString());
        slineFileDTO.setName(sLineFile.getName());
        slineFileDTO.setFileSize(sLineFile.getFileSize().toString());
        slineFileDTO.setType(sLineFile.getSuffix());
        slineFileDTO.setUrl(sLineFile.getStorePath());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        slineFileDTO.setCreateTime(simpleDateFormat.format(sLineFile.getGmtCreate()));
        return slineFileDTO;
    }
}
