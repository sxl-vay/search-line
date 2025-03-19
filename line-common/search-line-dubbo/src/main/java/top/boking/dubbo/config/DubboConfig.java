package top.boking.dubbo.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.logging.Logger;

@Configuration
@ImportResource(locations = {"classpath:dubbo-provider.xml"})
public class DubboConfig implements InitializingBean {

    private final Logger log = Logger.getLogger(DubboConfig.class.getName());

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("dubboConfig初始化完成");
    }
}
