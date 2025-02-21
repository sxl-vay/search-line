package top.boking.lock.config;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.boking.lock.DistributeLockAspect;

@Configuration
public class DistributeLockConfig {
    @Bean
    @ConditionalOnMissingBean
    public DistributeLockAspect distributeLockAspect(RedissonClient redissonClient) {
        return new DistributeLockAspect(redissonClient);
    }
}
