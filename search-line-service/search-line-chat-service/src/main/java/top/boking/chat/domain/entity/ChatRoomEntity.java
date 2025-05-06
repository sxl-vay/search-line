package top.boking.chat.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
public class ChatRoomEntity {
    private String roomId;
    private String roomName;
    private String roomDesc;
    private String roomAvatar;
    private String roomType;
    private String roomOwner;
    private String roomStatus;
    private String roomCreateTime;
    private String roomUpdateTime;
    private String roomDeleteTime;
    private ConcurrentHashMap<String/*memberId*/, ChatRoomMemberEntity> members;
}
