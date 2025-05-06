package top.boking.chat.infrastructure.handler;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.boking.chat.domain.dto.MessageDataDTO;
import top.boking.chat.domain.entity.ChatRoomEntity;
import top.boking.chat.domain.entity.ChatRoomMemberEntity;
import top.boking.chat.infrastructure.RoomHolder;

import java.util.Map;

@Component
@Slf4j
public class SocketIOEventHandler {

    private final SocketIOServer server;

    private final RoomHolder roomHolder;

    private final Map<String, ChatRoomEntity> rooms;

    public SocketIOEventHandler(SocketIOServer server, RoomHolder roomHolder) {
        this.server = server;
        this.roomHolder = roomHolder;
        this.rooms = roomHolder.getRooms();
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("🔌 用户连接: " + client.getSessionId());
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("用户 " + client.getSessionId() + " 断开");
        // 从所有房间中移除该用户
        for (Map.Entry<String, ChatRoomEntity> entry : rooms.entrySet()) {
            String sessionId = client.getSessionId().toString();
            ChatRoomEntity room = entry.getValue();
            Map<String, ChatRoomMemberEntity> members = room.getMembers();
            if (members.containsKey(sessionId)) {
                members.remove(sessionId);
                if (members.isEmpty()) {
                    rooms.remove(entry.getKey());
                }
            }
        }
    }


    @OnEvent("join-room")
    public void onJoinRoom(SocketIOClient client, String roomId) {
        client.joinRoom(roomId);
        log.info("用户 " + client.getSessionId() + " 加入展厅 " + roomId);

        // 初始化房间
        ChatRoomEntity roomEntity = ChatRoomEntity.builder().roomId(roomId).build();
        rooms.putIfAbsent(roomId, roomEntity);

        // 发送回调给加入的用户
        client.sendEvent("join-room", client.getSessionId().toString(), roomEntity);

        // 通知其他用户有新用户加入
        server.getRoomOperations(roomId).sendEvent("user-joined", client.getSessionId().toString());
    }

    @OnEvent("vr-message-sent")
    public void onVrMessageEvent(SocketIOClient client, MessageDataDTO<String> data) {
        log.info("VR消息: " + data);
        log.info("getAllRooms:{}", client.getAllRooms());
        // 广播VR消息
        BroadcastOperations roomOperations = server.getRoomOperations(data.getRoomId());
        //剔除掉发送者进行广播发送
        /*
        Collection<SocketIOClient> clients = roomOperations.getClients();
        for (SocketIOClient socketIOClient : clients) {
            if (socketIOClient.getSessionId().equals(client.getSessionId())) {
                continue;
            }
            socketIOClient.sendEvent("vr-message", data);
        }
        */
        roomOperations.sendEvent("vr-message", data);

    }


    @OnEvent("update-state")
    public void onUpdateState(SocketIOClient client, MessageDataDTO<String> data) {
        //todo

        // 获取用户所在的所有房间
        /*for (String roomId : client.getAllRooms()) {
            Map<String, ChatRoomEntity> rooms = roomHolder.getRooms();
            if (rooms.containsKey(roomId)) {
                rooms.get(roomId).put(client.getSessionId().toString(), data);
            }
        }*/
    }


}