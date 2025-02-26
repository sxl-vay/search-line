package top.boking.comment.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.boking.comment.domain.request.PageParam;

import java.util.List;

/**
 * 评论树形结构展示对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentTreeVO extends CommentVO {
    /**
     * 子评论列表
     */
    private List<CommentVO> subComments;

    /**
     * 回复列表
     */
    private List<CommentVO> replies;

    /**
     * 总评论数
     */
    private Long totalCount;

    /**
     * 分页参数
     */
    private PageParam pageParam;

    public CommentTreeVO(List<CommentVO> rootComments, PageParam pageParam) {
        this.subComments = rootComments;
        this.pageParam = pageParam;
    }
}