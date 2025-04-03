package top.boking.comment.domain.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.boking.base.domain.entity.BaseEntity;

@Data
@TableName("comment_content")
public class CommentContent extends BaseEntity {
    /**
     * 评论索引ID
     */
    private Long commentIndexId;

    /**
     * 评论内容
     */
    private String content;
}