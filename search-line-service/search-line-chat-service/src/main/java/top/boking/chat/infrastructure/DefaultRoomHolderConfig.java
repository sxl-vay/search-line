package top.boking.chat.infrastructure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.boking.chat.domain.entity.ChatRoomEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DefaultRoomHolderConfig {

    @Bean
    @ConditionalOnMissingBean(RoomHolder.class)
    public RoomHolder roomHolder() {
        return new DefaultRoomHolder();
    }

    private static class DefaultRoomHolder implements RoomHolder {
        private final ConcurrentHashMap<String, ChatRoomEntity> rooms = new ConcurrentHashMap<>();

        @Override
        public ChatRoomEntity getRoom(String roomId) {
            return rooms.get(roomId);
        }

        @Override
        public Map<String, ChatRoomEntity> getRooms() {
            return rooms;
        }

        @Override
        public void addRoom(ChatRoomEntity room) {
            rooms.putIfAbsent(room.getRoomId(), room);
        }
    }


}
