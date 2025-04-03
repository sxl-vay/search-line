package top.boking.comment.application.service;

import org.springframework.stereotype.Service;
import top.boking.comment.application.dto.CommentEntityDTO;
import top.boking.comment.domain.model.CommentContent;
import top.boking.comment.domain.service.CommentService;

import java.util.List;

@Service
public class CommentApplicationService {
    private final CommentService commentService;

    public CommentApplicationService(CommentService commentService) {
        this.commentService = commentService;
    }

    public Long publishComment(CommentEntityDTO commentEntityDTO) {
        return commentService.publishComment(commentEntityDTO);
    }

    public Long replyComment(CommentEntityDTO commentEntityDTO) {
        return commentService.replyComment(commentEntityDTO);
    }

    public List<CommentEntityDTO> getCommentList(String objId, Long rootId, Integer pageNum, Integer pageSize) {
        return commentService.getCommentList(objId, rootId, pageNum, pageSize);
    }

    public CommentContent getCommentContent(Long commentId) {
        return commentService.getCommentContent(commentId);
    }

    public Boolean likeComment(Long commentId, Long userId) {
        return commentService.likeComment(commentId, userId);
    }

    public Boolean unlikeComment(Long commentId, Long userId) {
        return commentService.unlikeComment(commentId, userId);
    }
}
