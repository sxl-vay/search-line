package top.boking.comment.domain.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.boking.comment.domain.model.CommentIndex;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentIndexMapper extends BaseMapper<CommentIndex> {
    List<Map<String, Object>> getSubCommentIdWithObjAndRootIds(String objId, List<Long> rootIds);
}