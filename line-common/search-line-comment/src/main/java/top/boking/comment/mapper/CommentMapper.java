package top.boking.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.boking.comment.domain.entity.Comment;
import top.boking.comment.domain.request.PageParam;

import java.util.List;

/**
 * 评论数据访问层接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    /**
     * 查询主评论列表（分页）
     *
     * @param postId 帖子ID
     * @param pageParam 分页参数
     * @return 主评论列表
     */
    List<Comment> selectRootComments(@Param("postId") Long postId, @Param("pageParam") PageParam pageParam);
    
    /**
     * 批量查询子评论
     *
     * @param rootIds 主评论ID列表
     * @return 子评论列表
     */
    List<Comment> selectSubComments(@Param("rootIds") List<Long> rootIds);
    
    /**
     * 批量查询回复评论
     *
     * @param parentIds 父评论ID列表
     * @return 回复评论列表
     */
    List<Comment> selectReplies(@Param("parentIds") List<Long> parentIds);
    
    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 影响行数
     */
    int insert(Comment comment);
    
    /**
     * 更新评论
     *
     * @param comment 评论信息
     * @return 影响行数
     */
    int update(Comment comment);
    
    /**
     * 删除评论（逻辑删除）
     *
     * @param id 评论ID
     * @return 影响行数
     */
    int delete(@Param("id") Long id);
    
    /**
     * 更新点赞数
     *
     * @param id 评论ID
     * @param likeCount 点赞数
     * @return 影响行数
     */
    int updateLikeCount(@Param("id") Long id, @Param("likeCount") Integer likeCount);
    
    /**
     * 根据ID查询评论
     *
     * @param id 评论ID
     * @return 评论信息
     */
    Comment selectById(@Param("id") Long id);
    
    /**
     * 查询用户的评论列表
     *
     * @param userId 用户ID
     * @param pageParam 分页参数
     * @return 评论列表
     */
    List<Comment> selectByUserId(@Param("userId") Long userId, @Param("pageParam") PageParam pageParam);
}