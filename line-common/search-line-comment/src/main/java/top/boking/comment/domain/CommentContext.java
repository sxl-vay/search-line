package top.boking.comment.domain;

import lombok.Data;
import top.boking.comment.domain.entity.Comment;
import top.boking.comment.domain.request.PageParam;

import java.util.List;
import java.util.Map;

// 评论处理上下文
@Data
public class CommentContext {
    private Long postId;              // 帖子ID
    private PageParam pageParam;      // 分页参数
    private List<Comment> rootComments; // 主评论列表
    private Map<Long, List<Comment>> subComments; // 子评论Map
    private Map<Long, List<Comment>> replyComments; // 回复Map
    // getter/setter省略
}