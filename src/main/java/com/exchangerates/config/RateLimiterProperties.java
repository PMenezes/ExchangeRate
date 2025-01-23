package com.exchangerates.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private long capacity = 100;       // Default value
    private long refillTimeMs = 60000; // Default value


    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getRefillTimeMs() {
        return refillTimeMs;
    }

    public void setRefillTimeMs(long refillTimeMs) {
        this.refillTimeMs = refillTimeMs;
    }
}
