package top.boking.comment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import top.boking.base.domain.entity.BaseEntity;

@Getter
@Setter
@TableName("user_like")
public class UserLike extends BaseEntity {
    private Long userId;
    private Long commentId;
}