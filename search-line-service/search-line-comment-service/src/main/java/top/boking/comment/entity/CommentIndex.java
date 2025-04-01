package top.boking.comment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.boking.base.domain.entity.BaseEntity;

@Data
@TableName("comment_index")
public class CommentIndex extends BaseEntity {
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
     * 根评论ID，如果是根评论则为0
     */
    private Long rootId;

    /**
     * 父评论ID，如果是根评论则为0
     */
    private Long parentId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 额外属性（JSON格式）
     */
    private String attrs;

}