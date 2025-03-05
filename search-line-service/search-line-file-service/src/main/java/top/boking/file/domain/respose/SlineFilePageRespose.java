package top.boking.file.domain.respose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.boking.file.domain.dto.SlineFileDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlineFilePageRespose {
    List<SlineFileDTO> files;

    Long total;

}
