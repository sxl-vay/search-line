package top.boking.chat.infrastructure;

import top.boking.chat.domain.entity.ChatRoomEntity;

import java.util.Map;

public interface RoomHolder {
    ChatRoomEntity getRoom(String roomId);

    Map<String, ChatRoomEntity> getRooms();

    void addRoom(ChatRoomEntity room);


}
