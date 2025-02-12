package com.kustacks.kuring.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CaffeineCacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        Arrays.stream(CacheType.values())
                .forEach(cacheType ->
                        cacheManager.registerCustomCache(
                                cacheType.getCacheName(),
                                Caffeine.newBuilder()
                                        .expireAfterAccess(cacheType.getExpireAfterSeconds(), TimeUnit.SECONDS)
                                        .maximumSize(cacheType.getMaximumSize())
                                        .build()));
        return cacheManager;
    }

    @Getter
    @AllArgsConstructor
    enum CacheType {
        VERIFICATION_CODE("verificationCodeCache", 5 * 60 * 1000, 1000);
        private final String cacheName;
        private final long expireAfterSeconds;
        private final int maximumSize;
    }
}
