package top.boking.chat.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class SocketIOConfig {

    @Value("${socket.ip:0.0.0.0}")
    private String ip;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(ip);
        config.setPort(30000);
        config.setOrigin("*");

        // 添加关键配置
        config.setPingTimeout(25000);
        config.setPingInterval(25000);
        config.setUpgradeTimeout(10000);

        // 设置传输方式
        config.setTransports(Transport.WEBSOCKET, Transport.POLLING);

        return new SocketIOServer(config);
    }
}