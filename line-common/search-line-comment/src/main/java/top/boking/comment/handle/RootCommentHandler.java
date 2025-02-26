package top.boking.comment.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.boking.comment.CommentHandler;
import top.boking.comment.domain.CommentContext;
import top.boking.comment.domain.entity.Comment;
import top.boking.comment.mapper.CommentMapper;

import java.util.List;
import java.util.stream.Collectors;

// 主评论处理器
@Component
public class RootCommentHandler implements CommentHandler {
    @Autowired
    private CommentMapper commentMapper;
    private CommentHandler nextHandler;

    @Override
    public void handle(CommentContext context) {
        // 1. 加载主评论（分页）
        List<Comment> roots = commentMapper.selectRootComments(
            context.getPostId(), 
            context.getPageParam()
        );
        context.setRootComments(roots);

        // 2. 收集主评论ID
        List<Long> rootIds = roots.stream()
            .map(Comment::getId)
            .collect(Collectors.toList());

        // 3. 触发下一个处理器
        if (nextHandler != null && !rootIds.isEmpty()) {
            nextHandler.handle(context);
        }
    }

    @Override
    public void setNext(CommentHandler handler) {
        this.nextHandler = handler;
    }
}
