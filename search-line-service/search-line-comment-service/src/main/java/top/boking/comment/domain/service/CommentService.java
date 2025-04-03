package top.boking.comment.domain.service;

import top.boking.comment.application.dto.CommentEntityDTO;
import top.boking.comment.domain.model.CommentContent;
import top.boking.comment.domain.model.CommentIndex;

import java.util.List;

public interface CommentService {
    /**
     * 发表评论
     *
     * @param objId   评论对象ID
     * @param userId  用户ID
     * @param content 评论内容
     * @return 评论ID
     */
    Long publishComment(CommentEntityDTO commentEntityDTO);

    /**
     * 回复评论
     *
     * @param objId    评论对象ID
     * @param userId   用户ID
     * @param content  评论内容
     * @param rootId   根评论ID
     * @param parentId 父评论ID
     * @return 评论ID
     */
    Long replyComment(CommentEntityDTO commentEntityDTO);

    /**
     * 获取评论列表
     *
     * @param objId 评论对象ID
     * @param page  页码
     * @param size  每页大小
     * @return 评论列表
     */
    List<CommentEntityDTO> getCommentList(String objId, Integer page, Integer size);

    /**
     * 获取评论内容
     *
     * @param commentId 评论ID
     * @return 评论内容
     */
    CommentContent getCommentContent(Long commentId);

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean likeComment(Long commentId, Long userId);

    /**
     * 取消点赞
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean unlikeComment(Long commentId, Long userId);
}