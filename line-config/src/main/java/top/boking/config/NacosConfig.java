package top.boking.config;


import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class NacosConfig {
    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;
    @Value("${spring.cloud.nacos.config.file-extension}")
    private String fileExtension;
    @Value("${spring.cloud.nacos.discovery.server-addr")
    private String discoveryServerAddr;
    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String discoveryNamespace;
}
