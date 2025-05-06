package top.boking.chat;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.boking.chat.infrastructure.handler.SocketIOEventHandler;

@SpringBootApplication
public class SearchLineChatServiceApplication implements CommandLineRunner {

    private final SocketIOServer server;
    private final SocketIOEventHandler eventHandler;

    public SearchLineChatServiceApplication(SocketIOServer server, SocketIOEventHandler eventHandler) {
        this.server = server;
        this.eventHandler = eventHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(SearchLineChatServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        server.addListeners(eventHandler);
        server.start();
    }
}
