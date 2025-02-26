package top.boking.comment.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.boking.comment.CommentHandler;
import top.boking.comment.domain.CommentContext;
import top.boking.comment.domain.entity.Comment;
import top.boking.comment.mapper.CommentMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 回复评论处理器
@Component
public class ReplyCommentHandler implements CommentHandler {
    @Autowired
    private CommentMapper commentMapper;
    private CommentHandler nextHandler;

    @Override
    public void handle(CommentContext context) {
        // 1. 收集所有子评论ID
        List<Long> parentIds = context.getSubComments().values().stream()
            .flatMap(List::stream)
            .map(Comment::getId)
            .collect(Collectors.toList());

        if (parentIds.isEmpty()) {
            return;
        }

        // 2. 批量查询回复
        List<Comment> replies = commentMapper.selectReplies(parentIds);

        // 3. 按父评论ID分组
        Map<Long, List<Comment>> replyMap = replies.stream()
            .collect(Collectors.groupingBy(Comment::getParentId));
        context.setReplyComments(replyMap);

        // 4. 触发下一个处理器（如果有的话）
        if (nextHandler != null) {
            nextHandler.handle(context);
        }
    }

    @Override
    public void setNext(CommentHandler handler) {
        this.nextHandler = handler;
    }
}