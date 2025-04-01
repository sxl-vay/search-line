package top.boking.escore.config;

import jakarta.annotation.PreDestroy;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.boking.escore.consts.ESMQConst;
import top.boking.file.consts.MQConst;

@Configuration
public class RocketMQPullConfig {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    private DefaultLitePullConsumer pullConsumer;

    // 初始化 Pull Consumer
    @Bean
    public DefaultLitePullConsumer initPullConsumer() throws Exception {
        pullConsumer = new DefaultLitePullConsumer(ESMQConst.DLQ_FILE_COMSUMERGROUP);
        pullConsumer.setNamesrvAddr(nameServer);
        pullConsumer.subscribe(MQConst.DLQ_PREFIX + ESMQConst.FILE_SYNC_COMSUMERGROUP, "*");  // 订阅 Topic 和 Tag
        pullConsumer.setPullBatchSize(10);
        pullConsumer.setAutoCommit(false);
        pullConsumer.start();
        return pullConsumer;
    }

    @Bean
    public DefaultMQAdminExt initMQAdminExt() throws Exception {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setNamesrvAddr(nameServer);
        defaultMQAdminExt.start();
        return defaultMQAdminExt;
    }

    // 销毁时关闭 Consumer
    @PreDestroy
    public void destroy() {
        if (pullConsumer != null) {
            pullConsumer.shutdown();
        }
    }
}