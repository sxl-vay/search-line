package top.boking.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.boking.file.domain.entity.SLineFile;

import java.util.List;

@Mapper
public interface SLineFileMapper extends BaseMapper<SLineFile> {
    List<SLineFile> selectWithDeleteType(Integer deleteType);
}
