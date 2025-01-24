package com.exchangerates.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to create an instance of a cache manager
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheExchangeRatesManager() {
        return new ConcurrentMapCacheManager("exchangeRateCache");
    }
}
