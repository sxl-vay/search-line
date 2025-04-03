package top.boking.comment.domain.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.boking.base.domain.entity.BaseEntity;

@Data
@TableName("comment_subject")
public class CommentSubject extends BaseEntity {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 对象ID
     */
    private String objId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评论回复总数
     */
    private Integer count;

    /**
     * 根评论总数
     */
    private Integer rootCount;
}