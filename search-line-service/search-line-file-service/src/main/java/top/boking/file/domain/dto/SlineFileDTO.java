package top.boking.file.domain.dto;

import lombok.Data;
import top.boking.file.domain.entity.SLineFile;

@Data
public class SlineFileDTO {
    private String id;
    private String name;
    private String size;
    private String type;
    private String url;
    private String createTime;

    public static SlineFileDTO buildWithSLineFile(SLineFile sLineFile) {
        SlineFileDTO slineFileDTO = new SlineFileDTO();
        slineFileDTO.setId(sLineFile.getId().toString());
        slineFileDTO.setName(sLineFile.getName());
        slineFileDTO.setSize(sLineFile.getFileSize().toString());
        slineFileDTO.setType(sLineFile.getSuffix());
        slineFileDTO.setUrl(sLineFile.getStorePath());
        slineFileDTO.setCreateTime(sLineFile.getGmtCreate().toString());
        return slineFileDTO;
    }
}
