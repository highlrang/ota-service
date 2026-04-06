package com.ota_service.ota_service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.ota_service.ota_service.service.AccommodationCacheNames;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(AccommodationCacheNames.POPULAR);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .maximumSize(100));
        return cacheManager;
    }
}
