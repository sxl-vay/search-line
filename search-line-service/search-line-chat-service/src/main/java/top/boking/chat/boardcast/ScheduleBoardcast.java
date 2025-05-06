package top.boking.chat.boardcast;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.boking.chat.handler.SocketIOEventHandler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class ScheduleBoardcast {

    private final SocketIOServer server;
    private final SocketIOEventHandler eventHandler;
    @Value("${socket.boardcast.period:-1}")
    private Long period;

    public ScheduleBoardcast(SocketIOServer server, SocketIOEventHandler eventHandler) {
        this.server = server;
        this.eventHandler = eventHandler;
    }

    @PostConstruct
    public void start() {
        if (period > 0) {
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
                    new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "schedule-boardcast"), new ThreadPoolExecutor.AbortPolicy());
            scheduledThreadPoolExecutor.scheduleAtFixedRate(eventHandler::broadcastRoomStates, 0, period, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }
}
