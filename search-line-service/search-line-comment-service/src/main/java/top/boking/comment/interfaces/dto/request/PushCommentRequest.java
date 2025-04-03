package top.boking.comment.interfaces.dto.request;

import lombok.Data;
import top.boking.comment.application.dto.CommentEntityDTO;
import top.boking.user.domain.entity.User;
import top.boking.user.utils.UserContext;

import java.io.Serializable;

@Data
public class PushCommentRequest implements Serializable {

    private String objId;
    private String content;
    private Long rootId;
    private Long parentId;

    public CommentEntityDTO toCommentEntityDTO() {
        return toCommentEntityDTO(UserContext.getCurrentUser());
    }

    public CommentEntityDTO toCommentEntityDTO(User user) {
        CommentEntityDTO commentEntityDTO = new CommentEntityDTO();
        commentEntityDTO.setObjId(objId);
        commentEntityDTO.setContent(content);
        commentEntityDTO.setParentId(parentId);
        commentEntityDTO.setRootId(rootId);
        commentEntityDTO.setUserId(user.getId());
        return commentEntityDTO;
    }
}
