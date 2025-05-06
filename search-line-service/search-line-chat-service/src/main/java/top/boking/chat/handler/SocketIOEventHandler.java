package top.boking.chat.handler;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.boking.chat.domain.dto.MessageDataDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SocketIOEventHandler {

    private final SocketIOServer server;
    private final Map<String, Map<String, Object>> rooms = new ConcurrentHashMap<>();

    public SocketIOEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("🔌 用户连接: " + client.getSessionId());
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("用户 " + client.getSessionId() + " 断开");
        // 从所有房间中移除该用户
        for (Map.Entry<String, Map<String, Object>> entry : rooms.entrySet()) {
            String sessionId = client.getSessionId().toString();
            if (entry.getValue().containsKey(sessionId)) {
                entry.getValue().remove(sessionId);
                if (entry.getValue().isEmpty()) {
                    log.info("销毁房间 " + entry.getKey());
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
        rooms.putIfAbsent(roomId, new ConcurrentHashMap<>());
        Map<String, Object> roomState = rooms.get(roomId);

        // 发送回调给加入的用户
        client.sendEvent("join-room", client.getSessionId().toString(), roomState);

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
        // 获取用户所在的所有房间
        for (String roomId : client.getAllRooms()) {
            if (rooms.containsKey(roomId)) {
                rooms.get(roomId).put(client.getSessionId().toString(), data);
            }
        }
    }


    // 定时广播房间状态
    public void broadcastRoomStates() {
        log.info("广播房间状态");
        rooms.forEach((roomId, state) -> {
            BroadcastOperations roomOperations = server.getRoomOperations(roomId);
            if (roomOperations.getClients().isEmpty()) {
                log.info("房间 " + roomId + " 已被销毁");
                rooms.remove(roomId);
            } else {
                log.info("房间 " + roomId + " 有 " + roomOperations.getClients().size() + " 个用户");
            }
            roomOperations.sendEvent("room-state", state);
        });
    }

}