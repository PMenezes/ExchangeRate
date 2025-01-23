package com.exchangerates.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class to create an instance of a cache manager
 */
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheExchangeRatesManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("exchangeRateCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES) // Cache expires after 1 minute
                .maximumSize(100)); // Maximum number of cache entries
        return cacheManager;
    }
}
