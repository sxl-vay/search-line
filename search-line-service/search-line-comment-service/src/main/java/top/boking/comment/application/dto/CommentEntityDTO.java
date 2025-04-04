package top.boking.comment.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentEntityDTO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String objId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String author;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long rootId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private Integer likeCount;

    private String content;

    private String attrs;

    private String gmtCreate;

    private String gmtModified;


}
