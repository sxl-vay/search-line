package top.boking.chat.domain.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MessageDataDTO<T> {
    private String roomId;
    private Map<String, String> properties;
    private T message;

    public MessageDataDTO() {
    }

    public static <T> MessageDataDTOBuilder<T> builder() {
        return new MessageDataDTOBuilder<>();
    }

    // 或者添加一个静态工厂方法
    public static MessageDataDTO fromString(String message) {
        return JSONObject.parseObject(message, MessageDataDTO.class);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public static final class MessageDataDTOBuilder<T> {
        private String roomId;
        private Map<String, String> properties;
        private T message;

        public MessageDataDTOBuilder<T> roomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public MessageDataDTOBuilder<T> properties(Map<String, String> properties) {
            this.properties = properties;
            return this;
        }

        public MessageDataDTOBuilder<T> message(T message) {
            this.message = message;
            return this;
        }

        public MessageDataDTO<T> build() {
            MessageDataDTO<T> messageDataDTO = new MessageDataDTO<>();
            messageDataDTO.setRoomId(roomId);
            messageDataDTO.setProperties(properties);
            messageDataDTO.setMessage(message);
            return messageDataDTO;
        }
    }
}
