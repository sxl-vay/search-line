package top.boking.chat.domain.entity;

import lombok.Data;

@Data
public class ChatRoomMemberEntity {
    private String memberId;
    private String memberName;
    private String memberAvatar;
    private String memberType;
    private String memberStatus;
}
