package top.boking.escore.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.boking.escore.domain.entity.FileTransferRecord;

@Mapper
public interface FileTransferRecordMapper extends BaseMapper<FileTransferRecord> {
}
