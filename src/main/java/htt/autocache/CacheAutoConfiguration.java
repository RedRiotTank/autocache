package htt.autocache;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


@Configuration
@EnableCaching
@ConditionalOnProperty(name = "cachelib.enabled", havingValue = "true", matchIfMissing = true)
public class CacheAutoConfiguration {


    @Value("${cachelib.max-size:1000}")
    private int maxSize;

    @Value("${cachelib.expire-after-write:10}")
    private int expireAfterWrite;

    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES));
        return cacheManager;
    }

}
