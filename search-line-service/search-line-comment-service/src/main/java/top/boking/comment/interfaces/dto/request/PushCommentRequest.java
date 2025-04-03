package top.boking.comment.interfaces.dto.request;

import lombok.Data;
import top.boking.comment.application.dto.CommentEntityDTO;

import java.io.Serializable;

@Data
public class PushCommentRequest implements Serializable {

    private String objId;
    private String content;
    private Long rootId;
    private Long parentId;

    public CommentEntityDTO toCommentEntityDTO() {
        CommentEntityDTO commentEntityDTO = new CommentEntityDTO();
        commentEntityDTO.setObjId(objId);
        commentEntityDTO.setContent(content);
        return commentEntityDTO;
    }
}
