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

// 子评论处理器
@Component
public class SubCommentHandler implements CommentHandler {
    @Autowired
    private CommentMapper commentMapper;
    private CommentHandler nextHandler;

    @Override
    public void handle(CommentContext context) {
        // 1. 批量查询子评论
        List<Long> rootIds = context.getRootComments().stream()
            .map(Comment::getId)
            .collect(Collectors.toList());

        List<Comment> subComments = commentMapper.selectSubComments(rootIds);

        // 2. 按父评论ID分组
        Map<Long, List<Comment>> subCommentMap = subComments.stream()
            .collect(Collectors.groupingBy(Comment::getParentId));
        context.setSubComments(subCommentMap);

        // 3. 触发下一个处理器
        if (nextHandler != null && !subComments.isEmpty()) {
            nextHandler.handle(context);
        }
    }

    @Override
    public void setNext(CommentHandler handler) {
        this.nextHandler = handler;
    }
}