package top.boking.chat.infrastructure.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.store.RedissonStoreFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class SocketIOConfig {

    @Value("${socket.ip:0.0.0.0}")
    private String ip;

    private static void setRedisConfig(Configuration config) {
        Config redisConfig = new Config();
        redisConfig.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(redisConfig);
//        redissonClient.getBucket("gggg").set("fffff");
//        redissonClient.getTopic("type").publish("msg");

        config.setStoreFactory(new RedissonStoreFactory(redissonClient));
    }

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

        setRedisConfig(config);
        // 设置传输方式
        config.setTransports(Transport.WEBSOCKET, Transport.POLLING);

        return new SocketIOServer(config);
    }
}