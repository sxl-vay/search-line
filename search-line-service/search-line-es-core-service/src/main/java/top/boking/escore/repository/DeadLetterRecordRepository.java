package top.boking.escore.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.boking.escore.entity.DeadLetterRecord;

@Mapper
public interface DeadLetterRecordRepository extends BaseMapper<DeadLetterRecord> {
}