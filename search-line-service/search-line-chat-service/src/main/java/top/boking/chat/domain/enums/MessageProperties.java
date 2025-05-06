package top.boking.chat.domain.enums;

public enum MessageProperties {
    SENDER("sender"),
    RECEIVER("receiver"),
    MESSAGE_TYPE("messageType"),
    MESSAGE_CONTENT("messageContent"),
    MESSAGE_TIME("messageTime"),
    MESSAGE_STATUS("messageStatus"),
    MESSAGE_ID("messageId"),
    MESSAGE_REPLY_ID("messageReplyId"),
    MESSAGE_REPLY_CONTENT("messageReplyContent"),
    MESSAGE_REPLY_TIME("messageReplyTime");

    private String value;

    private MessageProperties(String value) {
        this.value = value;
    }
}
