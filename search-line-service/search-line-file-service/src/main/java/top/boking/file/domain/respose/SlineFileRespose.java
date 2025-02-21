package top.boking.file.domain.respose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.boking.base.response.BaseResponse;
import top.boking.file.domain.entity.SLineFile;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlineFileRespose extends BaseResponse implements Serializable {
    private String  id;
    private String name;

    public static SlineFileRespose buildWithSLineFile(SLineFile sLineFile) {
        return new SlineFileRespose(String.valueOf(sLineFile.getId()), sLineFile.getName());
    }
}
