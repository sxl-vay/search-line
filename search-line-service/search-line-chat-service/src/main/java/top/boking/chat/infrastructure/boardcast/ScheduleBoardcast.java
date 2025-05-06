package top.boking.chat.infrastructure.boardcast;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.boking.chat.infrastructure.RoomHolder;
import top.boking.chat.infrastructure.handler.SocketIOEventHandler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class ScheduleBoardcast {

    private final SocketIOServer server;
    private final SocketIOEventHandler eventHandler;
    private final RoomHolder roomHolder;
    @Value("${socket.boardcast.period:-1}")
    private Long period;

    public ScheduleBoardcast(SocketIOServer server, SocketIOEventHandler eventHandler, RoomHolder roomHolder) {
        this.server = server;
        this.eventHandler = eventHandler;
        this.roomHolder = roomHolder;
    }

    @PostConstruct
    public void start() {
        if (period > 0) {
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
                    new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "schedule-boardcast"), new ThreadPoolExecutor.AbortPolicy());
            scheduledThreadPoolExecutor.scheduleAtFixedRate(this::broadcastRoomStates, 0, period, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }

    // 定时广播房间状态
    private void broadcastRoomStates() {
        log.info("广播房间状态");
        roomHolder.getRooms().forEach((roomId, state) -> {
            BroadcastOperations roomOperations = server.getRoomOperations(roomId);
            if (roomOperations.getClients().isEmpty()) {
                log.info("房间 " + roomId + " 已被销毁");
                roomHolder.getRooms().remove(roomId);
            } else {
                log.info("房间 " + roomId + " 有 " + roomOperations.getClients().size() + " 个用户");
            }
            roomOperations.sendEvent("room-state", state);
        });
    }
}
