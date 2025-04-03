package top.boking.comment.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.boking.comment.domain.model.CommentSubject;

@Mapper
public interface CommentSubjectMapper extends BaseMapper<CommentSubject> {
}