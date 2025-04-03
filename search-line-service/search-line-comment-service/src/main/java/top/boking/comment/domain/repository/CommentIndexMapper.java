package top.boking.comment.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.boking.comment.domain.model.CommentIndex;

@Mapper
public interface CommentIndexMapper extends BaseMapper<CommentIndex> {
}