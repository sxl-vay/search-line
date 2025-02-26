package top.boking.comment;

import top.boking.comment.domain.CommentContext;

// 评论处理器接口
public interface CommentHandler {
    void handle(CommentContext context);
    void setNext(CommentHandler handler);
}